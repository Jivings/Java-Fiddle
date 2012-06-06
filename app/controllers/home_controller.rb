class HomeController < ApplicationController
  # GET /
  def index
    @code = read('default', 'HelloWorld')
    @classpath = "default"
	  respond_to do |format|
	    format.html # index.html.erb
	  end
  end

  # GET /1
  def show
    @classpath = params[:id]
    classname = getClassname(@classpath)
    @code = read(@classpath, classname)
    render :action => "index"
  end

  def getClassname(uuid)
    compileData = Compile.where("uuid = '#{uuid}'" ).first
    if compileData
      return compileData[:classname]
    else
      return "No data"
    end
  end

  def read(dir, classname) 
    path = "#{Rails.root}/public/saved/#{dir}/#{classname}.java"
    return File.read(path)
  end
end
