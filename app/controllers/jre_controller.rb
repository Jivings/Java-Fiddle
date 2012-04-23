class JreController < ApplicationController
  # GET /
  def index
	@code = 'Code controller'
	respond_to do |format|
	  format.html # index.html.erb
	end
  end
  
  def thread
    send_file "#{Rails.root}/lib/assets/jre/workers/Thread.js", :type => 'text/javascript'
    #   render :js => "#{Rails.root}/lib/assets/jre/workers/Thread.js",
    #          :layout => false
               #:content_type => 'text/javascript'
  end

  # GET /jre/*/*/*
  def load
    classname = params[:other]
    if classname == 'jvm'
      send_file "#{Rails.root}/lib/assets/jre/#{classname}.js", :type => 'text/javascript'
    else
      send_file "#{Rails.root}/lib/assets/jre/#{classname}.class", :type => 'text/plain; charset=x-user-defined'
    end
  end

  def native
    native = params[:other]
    send_file "#{Rails.root}/lib/assets/jre/native/#{native}.js", :type => 'text/javascript'
  end
end
