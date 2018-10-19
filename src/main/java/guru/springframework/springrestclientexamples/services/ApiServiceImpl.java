package guru.springframework.springrestclientexamples.services;

import guru.springframework.api.domain.User;
import guru.springframework.api.domain.UserData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {

    // create a rest template to be used for parsing api services
    private RestTemplate restTemplate;

    // create the variable to be used for the service URI
    private final String api_url;

    // wire in the RestTemplate and value of api.url from application.properties file
    public ApiServiceImpl(RestTemplate restTemplate, @Value("${api.url}") String api_url) {
        this.restTemplate = restTemplate;
        this.api_url = api_url;
    }

    // service to get a limited number of users
    @Override
    public List<User> getUsers(Integer limit) {

        // create the uri to be used for the query
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                // get the initial uri string to be used (defined in application.properties and set above)
                .fromUriString(api_url)
                // tack on query parameters to the end of the URI
                .queryParam("limit", limit);

        // get a Rest object from the indicated URL created by the URI builder above
        // and bind the results to the UserData object
        UserData userData = restTemplate.getForObject(uriBuilder.toUriString(), UserData.class);
        return userData.getData();
    }

    // reactive method so that the getData method isn't implemented until the ThymeLeaf template calls for it
    @Override
    public Flux<User> getUsers(Mono<Integer> limit) {
        return WebClient
                // get the base URL
                .create(api_url)
                // use GET method
                .get()
                // use the uri builder and add query param
                .uri(uriBuilder -> uriBuilder.queryParam("limit", limit.block()).build())
                // accept JSON data
                .accept(MediaType.APPLICATION_JSON)
                // perform the actual data exchange (i.e. communicate with the service)
                .exchange()
                // convert the response to a Mono object and map that to the UserData class
                .flatMap(resp -> resp.bodyToMono(UserData.class))
                // return back a list of Users
                .flatMapIterable(UserData::getData);
    }
}
