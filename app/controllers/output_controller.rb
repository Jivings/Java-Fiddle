class OutputController < ApplicationController
  # GET /
  def index
	@classpath = "/saved/default/"
  @args = ""
	respond_to do |format|
	  format.html # index.html.erb
	end
  end

  # GET /1
  def show
    @uuid = params[:id]
    data = getClassData(@uuid);

    @classname = data[:classname]
    @args = data[:arguments]
    
    render :action => "index"
  end

    

  # get main classname
  def getClassData(uuid)
    compileData = Compile.where("uuid = '#{uuid}'" ).first
    if compileData
      return compileData
    else 
      return "No data"      
    end
  end

end
