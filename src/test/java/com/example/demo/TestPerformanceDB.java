package com.example.demo;

import com.example.StartupApplication;
import com.example.domain.BlogPO;
import com.example.mapper.BlogMapper;
import com.example.service.BlogService;
import com.example.util.BatchDBUtil;
import com.example.util.ProfilerUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName TestPerformanceDB
 * @Author gaotao
 * @Date 2020/7/2
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {StartupApplication.class})
public class TestPerformanceDB {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private ThreadPoolExecutor commonThreadPool;

    @Autowired
    private TransactionTemplate tx;

    @Autowired
    BatchDBUtil batchDBUtil;

    public BlogPO init(){
        BlogPO blog = new BlogPO();
        blog.setName("小明");
        blog.setAuthorName("123");
        return blog;
    }

    @Test
    public void insert(){
        ProfilerUtil.start("insert");
        BlogPO blog = this.init();
        blogMapper.insert(blog);
        ProfilerUtil.end("insert");
    }

//
//    @Test
//    public void batchInsert(){
//        ProfilerUtil.start("batchInsert");
//        List<BlogPO> list = new ArrayList<>();
//        BlogPO blog = this.init();
//        list.add(blog);
//        Function<Collection<BlogPO>, Boolean> fun =  blogService::saveBatch;
//        batchDBUtil.batchSliceOperate(fun,list,1000);
//        ProfilerUtil.end("batchInsert");
//    }

    @Test
    public void insertLoop(){
        ProfilerUtil.start("insertLoop");
        for(int i = 0; i< 10000; i++){
            BlogPO blog = this.init();
            blogMapper.insert(blog);
        }
        ProfilerUtil.end("insertLoop");
    }

    @Test
    public void batchInsert(){
        ProfilerUtil.start("batchInsert");
        List<BlogPO> lists = new ArrayList<>();
        for(int i = 0; i< 10000; i++){
            BlogPO blog = this.init();
            lists.add(blog);
        }
        blogService.saveBatch(lists);
        ProfilerUtil.end("batchInsert");
    }

    @Test
    public void batchInsertExecutor(){
        ProfilerUtil.start("batchInsertExecutor");
        List<BlogPO> lists = new ArrayList<>();
        for(int i = 0; i< 10000; i++){
            BlogPO blog = this.init();
            lists.add(blog);
        }
        saveBatch(lists);
        ProfilerUtil.end("batchInsertExecutor");
    }


    /**
     * 批量保存
     * @param lists
     */
    private void saveBatch(List<BlogPO> lists){
        if (CollectionUtils.isEmpty(lists)) {
            return;
        }
        int size = lists.size();
        int batchSize = 2500;
        int batchCount = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;

        AtomicInteger commitCount = new AtomicInteger();
        AtomicBoolean rollBack = new AtomicBoolean();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        List<BlogPO> blogList = new ArrayList<>(batchSize);
        int scStockRealPOSSize = 0;

        //async to db operation
        for (int i = 0; i < size; i++) {
            blogList.add(lists.get(i));
            scStockRealPOSSize++;

            if (batchSize != scStockRealPOSSize && size - 1 != i) {
                continue;
            }
            //async to batch insert
            List<BlogPO> batchWithTxScStockRealPOS = blogList;
            commonThreadPool.execute(() -> batchWithTx(batchWithTxScStockRealPOS, batchSize, commitCount, rollBack, countDownLatch));
            blogList = new ArrayList<>(batchSize);
            scStockRealPOSSize = 0;
        }

        //check all threads result
        while (commitCount.get() != batchCount && !rollBack.get()) {
        }
        //tell other all threads to continue
        countDownLatch.countDown();
    }

    /**
     * 手动控制事务
     * @param scStockRealPOS
     * @param batchSize
     * @param commitCount
     * @param rollBack
     * @param countDownLatch
     */
    private void batchWithTx(List<BlogPO> scStockRealPOS, int batchSize, AtomicInteger commitCount, AtomicBoolean rollBack, CountDownLatch countDownLatch) {
        tx.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //batch db operate
                batchInsert(scStockRealPOS, batchSize, commitCount, rollBack);
                //wait all end
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    rollBack.set(true);
                    log.error("saveBatch countDownLatch.await() error", e);
                    throw new RuntimeException("aveBatch countDownLatch.await() error", e);
                }
                //tx rollback for when need
                if (rollBack.get()) {
                    status.setRollbackOnly();
                }
            }
        });
    }


    /**
     * 插入数据库
     * @param batchSize
     * @param commitedNum
     * @param rollBack
     */
    private void batchInsert(List<BlogPO> blogList, int batchSize, AtomicInteger commitedNum, AtomicBoolean rollBack) {
        try {
            blogService.saveBatch(blogList, batchSize);
            commitedNum.incrementAndGet();
        } catch (Throwable e) {
            rollBack.set(true);
            log.error("asyncBatchInsert error", e);
        }
    }


}
