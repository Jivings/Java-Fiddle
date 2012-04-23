class HomeController < ApplicationController
  # GET /
  def index
	@code = read('sbpKhT', 'HelloWorld')
	@classpath = "/saved/sbpKhT/"
	respond_to do |format|
	  format.html # index.html.erb
	end
  end

  # GET /1
  def show
    uuid = params[:id]
    classname = getClassname(uuid)
    @code = read(uuid, classname)
    render :action => "index"
  end

  # get main classname
  def getClassname(uuid)
    compileData = Compile.where("uuid = '#{uuid}'" ).first
    if compileData
      return compileData[:classname]
    else 
      return "No data"      
    end
  end

  # read a file from saved space
  def read(dir, classname)
    path = "#{Rails.root}/public/saved/#{dir}/#{classname}.java"
    return File.readlines(path)
  end
end
