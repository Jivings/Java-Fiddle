class CreateCompiles < ActiveRecord::Migration
  def up
  end

  def down
  end

  def change
    create_table :compiles do |t|
      t.text :code
      t.string :classname
      t.string :uuid
      t.string :arguments

      t.timestamps
    end
  end
end
