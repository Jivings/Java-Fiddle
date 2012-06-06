# accordion control
$('.accordion').click( ->
  $(this).removeClass('last')
  $(this).next().toggle('fast')
  return false
).next().hide()
			
# codeMirror options
code = document.getElementById 'code'
codeMirror = CodeMirror.fromTextArea( code,
  onChange : ->
    $('#run-btn').addClass 'disabled'
    $('#build-run-btn').removeClass 'disabled'
    $('#build-btn').removeClass 'disabled'
  lineWrapping : true
)
		
####################
#  button handlers
#
classData = {
  classname : 'HelloWorld'
  uuid : 'default'
}


$('#save').click ->
  return  if $(this).hasClass 'disabled'
  $.post '/compiles/new',
    compile:
      code: codeMirror.getValue(),
      classname: "HelloWorld"
  ,(data) ->
    window.location.href = window.location.href.replace(window.location.pathname, "/#{data.uuid}")


run = ->
  return if $(this).hasClass 'disabled'
  $('iframe').attr('src', 'http://localhost:3000/code/' + classData.uuid)

build = (after) ->
  return if $(this).hasClass 'disabled'
  $('#working').css 'visibility', 'visible'
  $(this).addClass 'disabled'
  $('#build-run-btn').addClass 'disabled'
  $.post '/compiles/new',
		  compile :
        code : codeMirror.getValue(),
        classname : 'HelloWorld'
  ,(data) ->
    if data.classname?
      classData = data
      $('#run-btn').removeClass('disabled')
      $('#working').css('visibility','hidden')
      after()
    else
      error = data.error.join('')
      $('#terminal').html('<pre>'+error+'</pre>')
      $('#build-run-btn').removeClass('disabled')
      $('#build-btn').removeClass('disabled')

$('#build-btn').click build
$('#run-btn').click run
$('#build-run-btn').click ->
  build(run)

runJavaScriptJVM = (response) ->
  classname = response.classname if response
  if !response || response.result == 1
    new JVM
      stdin       : null,
      stdout      : 'terminal',
      stderr      : 'terminal',
      verbosity   : 'warn',
      classname   : classname,
      classpath   : "/jre",
      path        : window.classpath,
      workerpath  : "/jre/workers"
  	
    $('#run-btn').removeClass('disabled')
    $('#working').css('visibility', 'hidden')
			
  else
    error = response.error.replace(/\$/g, '\n')
    $('#terminal').html('<pre>'+error+'</pre>')
    $('#build-run-btn').removeClass('disabled')
    $('#build-btn').removeClass('disabled')
