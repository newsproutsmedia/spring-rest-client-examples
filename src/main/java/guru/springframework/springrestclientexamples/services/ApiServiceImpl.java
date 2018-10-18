package guru.springframework.springrestclientexamples.services;

import guru.springframework.api.domain.User;
import guru.springframework.api.domain.UserData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {

    // create a rest template to be used for parsing api services
    private RestTemplate restTemplate;

    // wire in the RestTemplate
    public ApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // service to get a limited number of users
    @Override
    public List<User> getUsers(Integer limit) {

        // get a Rest object from the indicated URL with indicated "limit"
        // and bind the results to the UserData object
        UserData userData = restTemplate.getForObject("http://apifaketory.com/api/user?limit=" + limit, UserData.class);
        return userData.getData();
    }
}
