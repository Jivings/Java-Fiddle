class Compile < ActiveRecord::Base
  attr_accessible :classname, :code, :error, :uuid

  validates :uuid, :uniqueness => { :case_sensitive => false }
  validates :uuid, :presence => true
  
  validates :classname, :presence => true
  validates :code, :presence => true
end
