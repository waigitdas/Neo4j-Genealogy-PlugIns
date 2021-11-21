/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.neo4jlib;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;

import java.util.HashMap;
import java.util.Spliterator;
import java.util.Spliterators;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.neo4j.graphdb.ResultTransformer;

/**
 *
 * @author david
 */
////    @Procedure(mode=Mode.READ)
//    
//    public Stream<SomeRecord> (String cq) {
//        try{Transaction ignored = graphDb.beginTx();
//            Result results = graphDb.execute(cq)) 
//                    { return toStream(results.columnAs("count").map(SomeRecords::new));
//                    }
//                    
//        }
    //}
    
//    private Stream<SomeRecord> toStream(ResourceIterator<SomeRecord> result) {
//        return StreamSupport.stream(
//                Spliterators.spliteratorUnknownSize(result,Spliterator.ORDERED)
//        )
//    }
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        // TODO code application logic here
//    }
//    
//}
