set :application, "Java Fiddle"
set :repository,  "git@github.com:Jivings/Java-Fiddle.git"

set :scm, :git
set :scm_username, "James Ivings"
set :scm_email, "james@ivings.org.uk"

# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

set :deploy_to, "/var/www/javafiddle"

role :web, "www.javafiddle.net"                          # Your HTTP server, Apache/etc
role :app, "www.javafiddle.net"                          # This may be the same as your `Web` server
role :db,  "www.javafiddle.net", :primary => true # This is where Rails migrations will run
#role :db,  "your slave db-server here"

# if you want to clean up old releases on each deploy uncomment this:
# after "deploy:restart", "deploy:cleanup"

# if you're still using the script/reaper helper you will need
# these http://github.com/rails/irs_process_scripts

# If you are using Passenger mod_rails uncomment this:
# namespace :deploy do
#   task :start do ; end
#   task :stop do ; end
#   task :restart, :roles => :app, :except => { :no_release => true } do
#     run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
#   end
# end
set :use_sudo, false
