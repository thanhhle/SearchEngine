package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An AndQuery composes other Query objects and merges their postings in an intersection-like operation.
 */
public class AndQuery implements Query {
	private List<Query> mChildren;
	
	public AndQuery(Collection<Query> children) {
		mChildren = new ArrayList<>(children);
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		List<Posting> result = mChildren.get(0).getPostings(index);
		
		// TODO: program the merge for an AndQuery, by gathering the postings of the composed QueryComponents and
		// intersecting the resulting postings.
		for(int i = 1; i < mChildren.size(); i++)
		{
			result = intersectList(result, mChildren.get(i).getPostings(index));
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return
		 String.join(" ", mChildren.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}

	
	private List<Posting> intersectList(List<Posting> list1, List<Posting> list2)
	{
		if(list1 == null || list2 == null)
		{
			return null;
		}

		List<Posting> result = new ArrayList<Posting>();
		
		int i = 0;
        int j = 0;
        
        while (i < list1.size() && j < list2.size()) 
        {
        	if(list1.get(i).getDocumentId() == list2.get(j).getDocumentId())
        	{
        		result.add(list1.get(i));
        		i++;
        		j++;
        	}
        	else if(list1.get(i).getDocumentId() < list2.get(j).getDocumentId())
        	{
        		i++;
        	}
        	else
        	{
        		j++;
        	}
        }
        
        return result;
	}
	
	/*
	private List<Posting> not(List<Posting> list1, Index index)
	{
		List<Posting> result = new ArrayList<Posting>();

		for(String term: index.getVocabulary())
		{
			List<Posting> list2 = index.getPostings(term);
			
			
			int i = 0;
	        int j = 0;
	        
	        for(Posting posting: result)
	        {
	        	for(Posting posting2: temp)
	        	{
	        		if(posting.getDocumentId())
	        	}
	        	
	        }
	        
	        while (i < list.size() && j < temp.size()) 
	        {
	        	if(list.get(i).getDocumentId() == temp.get(j).getDocumentId())
	        	{
	        		i++;
	        		j++;
	        	}
	        	else if(list1.get(i).getDocumentId() < list2.get(j).getDocumentId())
	        	{
	        		i++;
	        	}
	        	else
	        	{
	        		j++;
	        	}
	        }
	        
		}
		
		return result;
	}
	*/
}
