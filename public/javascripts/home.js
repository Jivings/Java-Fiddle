(function() {
  var build, classData, code, codeMirror, run, runJavaScriptJVM;
  $('.accordion').click(function() {
    $(this).next().toggle('fast');
    return false;
  }).next().hide();
  $('.open').click();
  code = document.getElementById('code');
  codeMirror = CodeMirror.fromTextArea(code, {
    onChange: function() {
      $('#run-btn').addClass('disabled');
      $('#build-run-btn').removeClass('disabled');
      return $('#build-btn').removeClass('disabled');
    },
    lineWrapping: true
  });
  classData = {
    classname: 'HelloWorld',
    uuid: 'default'
  };
  $('#save').click(function() {
    if ($(this).hasClass('disabled')) {
      return;
    }
    return $.post('/compiles/new', {
      compile: {
        code: codeMirror.getValue(),
        classname: "HelloWorld",
        arguments: $('#arguments').val()
      }
    }, function(data) {
      if (window.location.pathname === '/') {
        return window.location.href = window.location.href + ("" + data.uuid);
      } else {
        return window.location.href = window.location.href.replace(window.location.pathname, "/" + data.uuid);
      }
    });
  });
  run = function() {
    if ($(this).hasClass('disabled')) {
      return;
    }
    return $('iframe').attr('src', 'http://javafiddle.net/code/' + classData.uuid);
  };
  build = function(after) {
    if ($(this).hasClass('disabled')) {
      return;
    }
    $('#working').css('visibility', 'visible');
    $(this).addClass('disabled');
    $('#build-run-btn').addClass('disabled');
    return $.post('/compiles/new', {
      compile: {
        code: codeMirror.getValue(),
        classname: 'HelloWorld',
        arguments: $('#arguments').val()
      }
    }, function(data) {
      var error;
      if (data.classname != null) {
        classData = data;
        $('#run-btn').removeClass('disabled');
        $('#working').css('visibility', 'hidden');
        if (after) {
          return after();
        }
      } else {
        error = data.error.join('');
        $('#terminal').html('<pre>' + error + '</pre>');
        $('#build-run-btn').removeClass('disabled');
        return $('#build-btn').removeClass('disabled');
      }
    });
  };
  $('#build-btn').click(function() {
    return build(null);
  });
  $('#run-btn').click(run);
  $('#build-run-btn').click(function() {
    return build(run);
  });
  $('#clear-code-btn').click(function() {
    return codeMirror.setValue('');
  });
  runJavaScriptJVM = function(response) {
    var classname, error;
    if (response) {
      classname = response.classname;
    }
    if (!response || response.result === 1) {
      new JVM({
        stdin: null,
        stdout: 'terminal',
        stderr: 'terminal',
        verbosity: 'warn',
        classname: classname,
        classpath: "/jre",
        path: window.classpath,
        workerpath: "/jre/workers"
      });
      $('#run-btn').removeClass('disabled');
      return $('#working').css('visibility', 'hidden');
    } else {
      error = response.error.replace(/\$/g, '\n');
      $('#terminal').html('<pre>' + error + '</pre>');
      $('#build-run-btn').removeClass('disabled');
      return $('#build-btn').removeClass('disabled');
    }
  };
}).call(this);
