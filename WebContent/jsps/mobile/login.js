$(function() {
	if (!jQuery.cookie) {
		jQuery.cookie = function(name, value, options) {
			if (typeof value != 'undefined') {
				options = options || {};
				if (value === null) {
					value = '';
					options.expires = -1;
				}
				var expires = '';
				if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
					var date;
					if (typeof options.expires == 'number') {
						date = new Date();
						date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
					} else {
						date = options.expires;
					}
					expires = '; expires=' + date.toUTCString();
				}
				var path = options.path ? '; path=' + options.path : '';
				var domain = options.domain ? '; domain=' + options.domain : '';
				var secure = options.secure ? '; secure' : '';
				document.cookie = [ name, '=', encodeURIComponent(value), expires, path, domain, secure ].join('');
			} else {
				var cookieValue = null;
				if (document.cookie && document.cookie != '') {
					var cookies = document.cookie.split(';');
					for (var i = 0; i < cookies.length; i++) {
						var cookie = jQuery.trim(cookies[i]);
						if (cookie.substring(0, name.length + 1) == (name + '=')) {
							cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
							break;
						}
					}
				}
				return cookieValue;
			}
		};
	}
	$("#username").val($.cookie('username'));
	$("#password").val($.cookie('password'));
	if ($.cookie('master')) {
		$("#master").val($.cookie('master'));
	}
});

function mobileLogin() {
	$.post(basePath + "common/login.action", {
		username : $("#username").val(),
		password : $("#password").val(),
		language : 'zh_CN',
		sob : $("#master")[0].value
	}, function(data, textStatus) {
		var rs = $.evalJSON(data);
		if(rs.reason) {
			alert(rs.reason);
		} else if(rs.exceptionInfo) {
			alert(rs.exceptionInfo);
		} else {
			$.cookie('username', $("#username").val());
			$.cookie('password', $("#password").val());
			$.cookie('master', $("#master")[0].value);
			window.location.href = basePath + '?master=' + $("#master")[0].value;
		}
	}, "application/json");
}

$(document).ready(function() {
    $.ajax({
        url: basePath + 'common/cross.action',
        type: 'GET',
        data: {
            path: 'http://113.105.74.141:8081/artifactory/libs-release-local/com/uas/android/pm/maven-metadata.xml'
        },
        dataType: 'xml',
        success: function(data) {
            if (data) {
            	// latest version
                var version = $(data).find("metadata").find("versioning").find("latest").text();
                $('#download_client').attr('href', '//113.105.74.141:8081/artifactory/libs-release-local/com/uas/android/pm/' + version + '/pm-' + version + '.apk');
            }
        }
    });
});