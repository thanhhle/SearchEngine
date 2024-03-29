package cecs429.index;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import cecs429.text.TokenProcessor;


public class DiskIndexWriter
{
	public void writeIndex(Index index, String directoryPath, TokenProcessor processor)
	{
		// Get the file named "postings.bin" which is to store the index's postings
		File postingsFile = new File(directoryPath, "postings.bin");

		// Get the file named "vocab_table.bin" which is to store mapping from vocabulary term to byte position in postings.bin
		File vocabTableFile = new File(directoryPath, "vocab_table.db");

		DB vocabTableDB = DBMaker.fileDB(vocabTableFile).fileMmapEnable().closeOnJvmShutdown().make();
		BTreeMap<String, Long> vocabTable = vocabTableDB.treeMap("map")
											.keySerializer(Serializer.STRING)
											.valueSerializer(Serializer.LONG).create();

		List<String> vocabulary = index.getVocabulary();

		try
		{
			long byteOffset = 0;
			for (String term : vocabulary)
			{
				DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(postingsFile, true)));

				// Add the byte position where the posting begins
				vocabTable.put(term, byteOffset);

				// Get the list of the posting of each vocabulary term
				List<Posting> postings = index.getPostings(term, true);

				int docFreq = postings.size();

				// Write the dft to the file
				outputStream.writeInt(docFreq);

				int lastDocId = 0;
				for (Posting posting : postings)
				{
					int docId = posting.getDocumentId();

					// Write the docId using gap to the file
					outputStream.writeInt(docId - lastDocId);

					// Update the last doc Id
					lastDocId = docId;

					List<Integer> positions = posting.getPositions();

					int posFreq = positions.size();

					// Write the tftd to the file
					outputStream.writeInt(posFreq);

					int lastPos = 0;
					for (Integer pos : positions)
					{
						// Write the position using gap to the file
						outputStream.writeInt(pos - lastPos);

						// Update the last position
						lastPos = pos;
					}
				}

				// Update the byte position of where the next posting begins
				byteOffset += outputStream.size();

				outputStream.close();
			}
		}

		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		vocabTableDB.close();
	}


	public void writeDocWeights(double weight, String directoryPath)
	{
		// Get the file named "docWeights.bin" which is to store the index's postings
		File docWeightsFile = new File(directoryPath, "docWeights.bin");

		// Write the doc weight to the file
		try
		{
			DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(docWeightsFile, true)));
			outputStream.writeDouble(weight);
			outputStream.close();
		}

		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	public void writeKGramIndex(KGramIndex kgramIndex, String directoryPath)
	{
		// Get the file named "candidates.bin" which is to store the kgram index's candidates
		File candidatesFile = new File(directoryPath, "candidates.bin");

		// Get the file named "vocab_table.bin" which is to store mapping from kgram term to byte position of its list of candidates
		File kgramTableFile = new File(directoryPath, "kgram_table.db");

		DB kgramTableDB = DBMaker.fileDB(kgramTableFile).fileMmapEnable().closeOnJvmShutdown().make();
		BTreeMap<String, Long> kgramTable = kgramTableDB.treeMap("treemap")
											.keySerializer(Serializer.STRING)
											.valueSerializer(Serializer.LONG).create();
		try
		{
			long byteOffset = 0;
			for (String kgram : kgramIndex.getKGrams())
			{
				DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(candidatesFile, true)));

				// Add the byte position where the list of candidates begins
				kgramTable.put(kgram, byteOffset);

				// Get the list of candidates of each kgram
				List<String> candidates = kgramIndex.getCandidates(kgram);

				int candidateFreq = candidates.size();

				// Write the dft to the file
				outputStream.writeInt(candidateFreq);

				for (String candidate : candidates)
				{
					// Get the bytes of each candidate in UTF-8
					byte[] bytes = candidate.getBytes(StandardCharsets.UTF_8);

					// Write the length of that byte array to the file
					outputStream.writeInt(bytes.length);

					// Write the bytes to the file
					outputStream.write(bytes);
				}

				// Update the byte position of where the next candidate begins
				byteOffset += outputStream.size();

				outputStream.close();
			}
		}

		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		kgramTableDB.close();
	}
}
