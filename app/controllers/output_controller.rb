class OutputController < ApplicationController
  # GET /
  def index
	@classpath = "/saved/default/"
	respond_to do |format|
	  format.html # index.html.erb
	end
  end

  # GET /1
  def show
    @uuid = params[:id]
    @classname = getClassname(@uuid)
    
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

end
