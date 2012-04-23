require 'test_helper'

class CompilesControllerTest < ActionController::TestCase
  setup do
    @compile = compiles(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:compiles)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create compile" do
    assert_difference('Compile.count') do
      post :create, :compile => { :classname => @compile.classname, :code => @compile.code }
    end

    assert_redirected_to compile_path(assigns(:compile))
  end

  test "should show compile" do
    get :show, :id => @compile
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @compile
    assert_response :success
  end

  test "should update compile" do
    put :update, :id => @compile, :compile => { :classname => @compile.classname, :code => @compile.code }
    assert_redirected_to compile_path(assigns(:compile))
  end

  test "should destroy compile" do
    assert_difference('Compile.count', -1) do
      delete :destroy, :id => @compile
    end

    assert_redirected_to compiles_path
  end
end
