package io.github.stack07142.rxjava_android_samples.retrofit;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {

    @GET("/repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> contributors(
            @Path("owner") String owner, @Path("repo") String repo);

    @GET("/users/{user}")
    Observable<User> user(@Path("user") String user);

    class Contributor {
        public String login;
        public long contributions;
    }

    class User {
        public String name;
        public String email;
    }
}
