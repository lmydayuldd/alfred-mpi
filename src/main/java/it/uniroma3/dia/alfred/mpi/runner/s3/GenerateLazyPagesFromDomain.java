package it.uniroma3.dia.alfred.mpi.runner.s3;

import it.uniroma3.dia.alfred.mpi.model.DomainHolder;
import it.uniroma3.dia.alfred.mpi.model.constants.DomainHolderKeys;
import it.uniroma3.dia.datasource.IncrementalIdRetriever;
import it.uniroma3.dia.datasource.s3.S3Uploader;

import java.util.List;

import model.Page;

import com.google.common.collect.Lists;

public class GenerateLazyPagesFromDomain {
	private GenerateLazyPagesFromDomain() {}

	public static List<Page> runDownloader(DomainHolder domainConf) {
		List<String> uriToRetrieve = Lists.newArrayList();
		
		IncrementalIdRetriever iidFake = new IncrementalIdRetriever();
		String siteValue = domainConf.getConfigurationValue(DomainHolderKeys.SITE_KEY);
		String domainValue = domainConf.getConfigurationValue(DomainHolderKeys.DOMAIN_ID_KEY);
		String bucketValue = domainConf.getConfigurationValue(DomainHolderKeys.BUCKET_S3_KEY);
		
		if ( siteValue.equalsIgnoreCase(DomainHolderKeys.KnownSites.IMDB.getKey()) && 
				( domainValue.equalsIgnoreCase(DomainHolderKeys.KnownDomains.FILM.getKey()) || 
						domainValue.equalsIgnoreCase(DomainHolderKeys.KnownDomains.ACTORS.getKey()) )) {
			// IMDB
			if ( domainValue.equalsIgnoreCase(DomainHolderKeys.KnownDomains.FILM.getKey()) ) {
				uriToRetrieve.addAll( iidFake.getAllMoviesURL() );
			} else {
				uriToRetrieve.addAll( iidFake.getAllActorsURL() );
			}
		} else if ( siteValue.equalsIgnoreCase(DomainHolderKeys.KnownSites.ALLMUSIC.getKey()) && 
				( domainValue.equalsIgnoreCase(DomainHolderKeys.KnownDomains.ALBUMS.getKey()) || 
						domainValue.equalsIgnoreCase(DomainHolderKeys.KnownDomains.ARTIST.getKey()) )) {
			// AllMusic
			if ( domainValue.equalsIgnoreCase(DomainHolderKeys.KnownDomains.ALBUMS.getKey()) ) {
				uriToRetrieve.addAll( iidFake.getAllAlbumsURL() );
			} else {
				uriToRetrieve.addAll( iidFake.getAllArtistsURL() );
			}
		} else {
			List<String> tempUris = null;
			try {
				tempUris = S3Uploader.getInstance().getObjectUrls(bucketValue);
			} catch(Exception e) {
				tempUris = null;
			}
			uriToRetrieve.addAll(tempUris);
		}
		
		// System.out.println(uriToRetrieve);

		return convertUriToPage(uriToRetrieve, domainConf.getConfigurationValue(DomainHolderKeys.BUCKET_S3_KEY));
	}
	
	private static List<Page> convertUriToPage(List<String> uriList, String bucketName) {
		List<Page> pagesReturn = Lists.newLinkedList();
		
		for(String uri: uriList) {
			pagesReturn.add(new LazyPageS3ProxyMPI(uri, bucketName));
		}
		
		return pagesReturn;
	}
}
