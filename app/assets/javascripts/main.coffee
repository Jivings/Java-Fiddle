# accordion control
$('.accordion').click( ->
  $(this).next().toggle('fast')
  return false
).next().hide()
	
$('.open').click()
# codeMirror options
code = document.getElementById 'code'
codeMirror = CodeMirror.fromTextArea( code,
  onChange : ->
    $('#run-btn').addClass 'disabled'
    $('#build-run-btn').removeClass 'disabled'
    $('#build-btn').removeClass 'disabled'
    $('#classname').val(codeMirror.getClassname())
  lineWrapping : true
  matchBrackets : true
  lineNumbers : true
  mode : "text/x-java"
  autofocus: true
  gutter: true
)

codeMirror.getClassname = () ->
  search = @getSearchCursor new RegExp(' *(public)? +(abstract +|final +)?class +([^ ]+)')
  found = search.findNext()
  return found[3]

#####################
#  button handlers  #
#####################

classData = {
  classname : 'HelloWorld'
  uuid : 'default'
}


###
$('#save').click ->
  return  if $(this).hasClass 'disabled'
  $.post '/new',
    compile:
      code: codeMirror.getValue(),
      classname: codeMirror.getClassname(),
      arguments: $('#arguments').val()
  ,(data) ->
    if window.location.pathname is '/'
      window.location.href = window.location.href + "#{data.uuid}"
    else
      window.location.href = window.location.href.replace(window.location.pathname, "/#{data.uuid}")
###


run = ->
  $.post('/compile',
  {
    classname : $('#classname').val()
    code : codeMirror.getValue()
    arguments : $('#arguments').val()
  },
  (classID) ->
    $('iframe').attr('src', '/run/' + classID)
  )


build = (after) ->
  # fixme
  alert('not implemented')
  return if $(this).hasClass 'disabled'
  $('#working').css 'visibility', 'visible'
  $(this).addClass 'disabled'
  $('#build-run-btn').addClass 'disabled'
  $.post '/compiles/new',
		  compile :
        code : codeMirror.getValue(),
        classname : codeMirror.getClassname(),
        arguments : $('#arguments').val()
  ,(data) ->
    if data.classname?
      classData = data
      $('#compile-error').hide()
      $('#run-btn').removeClass('disabled')
      $('#working').css('visibility','hidden')
      if after then after()
    else
      error = data.error.join('')
      $('#compile-error').text(error).show()
      $('iframe').hide()
      $('#build-run-btn').removeClass('disabled')
      $('#build-btn').removeClass('disabled')

$('#build-btn').click -> build(null)
$('#run-btn').click run
$('#build-run-btn').click ->
  build(run)

$('#clear-code-btn').click ->
  codeMirror.setValue('')
    

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
