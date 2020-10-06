package cecs429.index;

import java.util.List;

/**
 * An Index can retrieve postings for a term from a data structure associating terms and the documents
 * that contain them.
 */
public interface Index {
	/**
	 * Retrieves a list of Postings of documents that contain the given term.
	 */
	List<Posting> getPostings(String term);
	
	
	/**
	 * Retrieves a list of Postings of documents that contain the given term.
	 */
	List<Posting> getPostings(List<String> terms);
	
	
	/**
	 * A (sorted) list of all terms in the index vocabulary.
	 */
	List<String> getVocabulary();
	
	
	/**
	 * Retrieve the K-gram index.
	 */
	void buildKGramIndex();
	
	
	/**
	 * Retrieve the K-gram index.
	 */
	KGramIndex getKGramIndex();

}
