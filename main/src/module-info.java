module atunstall.server.http {
    requires atunstall.server.core;
    requires atunstall.server.io;
    exports atunstall.server.http.api;
    exports atunstall.server.http.impl to atunstall.server.core;
}