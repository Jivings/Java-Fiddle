class CodeController < ApplicationController
  # GET /
  def index
	@code = 'Code controller'
	respond_to do |format|
	  format.html # index.html.erb
	end
  end

  # GET /1
  def show
	@code = 'Code controller with show'
 	respond_to do |format|
	  format.html # home#index
	end
  end
end
