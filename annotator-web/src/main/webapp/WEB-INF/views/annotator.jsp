<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en" ng-app="annotator">
    <head>
        <meta charset="UTF-8"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="css/annotator.css" rel="stylesheet"/>
    </head>
    <body>
        <nav class="navbar navbar-default navbar-static-top">
            <div class="container">
                <div class="navbar-header">
                    <span class="navbar-brand">Annotator <sup class="beta">BETA</sup></span>
                </div>
                <ul class="nav navbar-nav">
                    <li><a href="#/about">About</a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown">
                        <a class="dropdown-toggle" data-toggle="dropdown" role="button">
                            Signed in as <sec:authentication property="name"/> (<sec:authentication property="credentials.providerName"/>) <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="logout">Logout</a>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
        
        <div class="container">
            <div ng-view></div>
        </div>
        
        <script src="//code.jquery.com/jquery-2.1.3.min.js"></script>
        <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.3.15/angular.min.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.3.15/angular-route.min.js"></script>
        <script type="text/javascript">
        // workaround for a cleaner url
        var newUri = location.protocol + "//" + location.host + location.pathname;
        window.history.replaceState({}, document.title, newUri);
        
        window.ANNOTATION = angular.fromJson('${metaData}');
        console.log(ANNOTATION);
        </script>
        <script src="js/annotator.js"></script>
    </body>
</html>