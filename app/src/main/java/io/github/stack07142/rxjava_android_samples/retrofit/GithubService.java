package io.github.stack07142.rxjava_android_samples.retrofit;

import java.security.acl.Owner;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GithubService {

    @GET("search/repositories?sort=stars&order=desc")
    Observable<Repositories> listRepos(@Query("q") String query);

    public static class Repositories {

        public final List<RepositoryItem> items;

        public Repositories(List<RepositoryItem> items) {

            this.items = items;
        }
    }

    public static class RepositoryItem {

        public final String description;
        public final Owner owner;
        public final String language;
        public final String name;
        public final String stargazers_count;
        public final String forks_count;
        public final String full_name;
        public final String html_url;


        public RepositoryItem(String description, Owner owner, String language, String name, String stargazers_count, String forks_count, String full_name, String html_url) {
            this.description = description;
            this.owner = owner;
            this.language = language;
            this.name = name;
            this.stargazers_count = stargazers_count;
            this.forks_count = forks_count;
            this.full_name = full_name;
            this.html_url = html_url;
        }
    }

    public static class Owner {
        public final String received_events_url;
        public final String organizations_url;
        public final String avatar_url;
        public final String gravatar_id;
        public final String gists_url;
        public final String starred_url;
        public final String site_admin;
        public final String type;
        public final String url;
        public final String id;
        public final String html_url;
        public final String following_url;
        public final String events_url;
        public final String login;
        public final String subscriptions_url;
        public final String repos_url;
        public final String followers_url;

        public Owner(String received_events_url, String organizations_url, String avatar_url, String gravatar_id, String gists_url, String starred_url, String site_admin, String type, String url, String id, String html_url, String following_url, String events_url, String login, String subscriptions_url, String repos_url, String followers_url) {
            this.received_events_url = received_events_url;
            this.organizations_url = organizations_url;
            this.avatar_url = avatar_url;
            this.gravatar_id = gravatar_id;
            this.gists_url = gists_url;
            this.starred_url = starred_url;
            this.site_admin = site_admin;
            this.type = type;
            this.url = url;
            this.id = id;
            this.html_url = html_url;
            this.following_url = following_url;
            this.events_url = events_url;
            this.login = login;
            this.subscriptions_url = subscriptions_url;
            this.repos_url = repos_url;
            this.followers_url = followers_url;
        }
    }

}
