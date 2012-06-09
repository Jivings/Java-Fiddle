# Handles webpage requests. 
# By default will respond with the HelloWorld 
# demo.
# If a UUID is provided then the code at that location
# will be returned.

class HomeController < ApplicationController
  # GET /
  def index
    if params[:id].nil?
       @classpath = "default"
    else
       @classpath = params[:id]
    end
    classData = getClassData(@classpath)
    @args = classData[:arguments]
    @code = read(@classpath, classData[:classname])
    # if class is uncompilable, this will exist
    @compileError = classData[:error]
    respond_to do |format|
      format.html # index.html.erb
    end
  end

  # GET /1
  # UUID provided. Process identical to above except 
  # classpath is preset as UUID.
  def show
    @classpath = params[:id]
    index()
  end

  # Retrieves Class information from database
  def getClassData(uuid)
    # returns a list. Should only be one entry so call `first`
    compileData = Compile.where("uuid = '#{uuid}'" ).first
    if compileData
      return compileData
    else
      return nil
    end
  end

  # Retrieves code from disk
  def read(dir, classname) 
    path = "#{Rails.root}/public/saved/#{dir}/#{classname}.java"
    return File.read(path)
  end
end
