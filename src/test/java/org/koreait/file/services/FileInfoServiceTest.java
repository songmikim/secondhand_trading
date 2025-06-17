package org.koreait.file.services;

import org.junit.jupiter.api.Test;
import org.koreait.file.entities.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FileInfoServiceTest {
    @Autowired
    private FileInfoService service;

    @Test
    void test (){
       // FileInfo item = service.get(1L);
       // System.out.println(item);
        String gid = "62fd8a4f-a8a0-4c22-97ca-27dbd295a250";
        List<FileInfo> items = service.getList(gid, null);
        items.forEach(System.out::println);
    }
}
