package org.koreait.file.repositories;

import org.koreait.file.entities.FileInfo;
import org.springframework.data.repository.ListCrudRepository;

public interface FileInfoRepository  extends ListCrudRepository<FileInfo , Long> {
}
