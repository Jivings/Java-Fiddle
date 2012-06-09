# Handle code compilation and creation of
# Unique Identifiers for compiled classes
class CompilesController < ApplicationController

  # GET /compiles/new
  # GET /compiles/new.json
  def new
    @compile = Compile.new(params[:compile])
    logger.debug 'Save'
    stdin, stdout, stderr = compileAndSave()
    logger.debug stderr.gets
    logger.debug stdout.gets

    respond_to do |format|
      if @compile.save! and stderr.gets == nil
        format.json { render :json => @compile }
      else
        @compile[:error] = stderr
        format.json { render :json => @compile.to_json(:only => [:error]), status => :ok }
      end
    end
  end

  # POST /compiles
  # POST /compiles.json
  def create
    @compile = Compile.new(params[:compile])
    logger.debug 'Temp'
    stdin, stdout, stderr = compileTemp()
    logger.debug stderr
    respond_to do |format|
      if stderr.gets == nil
        format.json { render :json => @compile.to_json(:only => [:classname, :location, :id, :uuid]), :status => :created, :location => @compile }
      else
        @compile[:error] = stderr
        format.json { render :json => @compile.to_json(:only => [:error]) , :status => :ok }
      end
    end
  end

  
  def uuid
    o =  [('a'..'z'),('A'..'Z')].map{|i| i.to_a}.flatten;  
    string  =  (0..5).map{ o[rand(o.length)]  }.join;
    return string
  end
  
  def compileAndSave() 
    @compile[:uuid] = uuid
    dir = "#{Rails.root}/public/saved/#{@compile[:uuid]}"
    Dir.mkdir( dir )
    return compile(dir)
  end
 
  def compileTemp()
    @compile[:uuid] = uuid
    dir =  "#{Rails.root}/public/tmp/#{@compile[:uuid]}"
    Dir.mkdir( dir )
    return compile(dir)
  end

  def compile(dir)
    file = "#{dir}/#{@compile[:classname]}.java"
    File.open(file, "w+"  ) do |f|
      f.puts @compile[:code]
    end
    return Open3.popen3("javac #{file}")
  end

  # DELETE /compiles/1
  # DELETE /compiles/1.json
  def destroy
    @compile = Compile.find(params[:id])
    @compile.destroy

    respond_to do |format|
      format.html { redirect_to compiles_url }
      format.json { head :no_content }
    end
  end
end
