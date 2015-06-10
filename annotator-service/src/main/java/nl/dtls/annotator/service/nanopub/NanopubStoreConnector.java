package nl.dtls.annotator.service.nanopub;

import java.net.URI;

import org.nanopub.Nanopub;

public interface NanopubStoreConnector {
    URI storeNanopub(Nanopub nanopub);
}
