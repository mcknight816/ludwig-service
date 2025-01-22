package com.bluntsoftware.ludwig.conduit.service.nosql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoSqlResult  {
    long currpage;
    long totalpages;
    long totalrecords;
    List<Document> rows;
}
