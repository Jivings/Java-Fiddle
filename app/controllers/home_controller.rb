class HomeController < ApplicationController
  # GET /
  def index
    if @classpath.nil?
      @classpath = "default"
    end
    classData = getClassData(@classpath)
    @args = classData[:arguments]
    @code = read(@classpath, classData[:classname])

	  respond_to do |format|
	    format.html # index.html.erb
	  end
  end

  # GET /1
  def show
    @classpath = params[:id]
    index()
  end

  def getClassData(uuid)
    compileData = Compile.where("uuid = '#{uuid}'" ).first
    if compileData
      return compileData
    else
      return "No data"
    end
  end

  def read(dir, classname) 
    path = "#{Rails.root}/public/saved/#{dir}/#{classname}.java"
    return File.read(path)
  end
end
