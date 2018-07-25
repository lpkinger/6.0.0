function toogleAppCss(id) {
	$('#app-container .info').css('display', 'none');
	$('#info-' + id).css('display', 'block');
	$('ul .active').removeClass('active');
	$('#link-' + id).parent().addClass('active');
}
function itemClick(type, master, id, t) {
	if (master != null && master != '' && master != 'null') {
		$.post(basePath + "common/changeMaster.action", {
			to : master
		}, function(result, text) {
			var rs = $.evalJSON(result);
			if (!rs.success) {
				alert("在" + desc + "未找到您的账号.");
			} else {
				deal(type, id, t);
			}
		}, "application/json");
	} else {
		deal(type, id, t);
	}
}
function deal(type, id, t) {
	$('.info .active').removeClass('active');
	$('#info-item-' + type + '-' + id).addClass('active');
	$('#next').css('display', 'block');
	var frame = $('#frame-container iframe');
	$('#footer-container').css('display', 'none');
	$('#app-container').css('display', 'none');
	$('#frame-container').css('display', 'block');
	if ('flow' == type)
		frame.attr('src', basePath + "jsps/common/jprocessDeal.jsp?_noc=1&formCondition=jp_nodeIdIS" + id);
	else if ('procand' == type)
		frame.attr('src', basePath + "jsps/common/jtaketask.jsp?_noc=1&formCondition=jp_nodeIdIS" + id);
	else
		frame.attr('src', basePath + "jsps/plm/record/billrecord.jsp?_noc=1&ra_id=" + id + "&ra_type=" + t);
}
function logout() {
	confirm("要退出系统吗?") && $.get(basePath + "common/logout.action", function(result) {
		if (result.exceptionInfo) {
			alert(rs.exceptionInfo);
		} else {
			window.location.reload();
		}
	});
}
function changeMaster(name, desc) {
	$('#header-container ul .active').removeClass('active');
	$('#' + name).addClass('active');
	var bool = confirm("要切换到" + desc + "吗?");
	if (!bool) {
		$('#app').removeClass('active');
		$('#header-container ul').removeClass('active');
	} else {
		$.post(basePath + "common/changeMaster.action", {
			to : name
		}, function(result, text) {
			var rs = $.evalJSON(result);
			if (!rs.success) {
				alert("在" + desc + "未找到您的账号.");
			} else {
				window.location.href = basePath + "?master=" + name;
			}
		}, "application/json");
	}
}
function next() {
	var type = null, id = null, master = null,t = null;
	var item = $('.info .active').next();
	if(item.length > 0) {
		type = item.attr('data-type');
		var data = toJSON(item.attr('data'));
		id = type == 'flow' || type == 'procand' ? data.jp_nodeId : data.ra_id;
		master = data.CURRENTMASTER;
		t = type == 'task' ? data.ra_type : null;
	}
	if(id != null)
		itemClick(type, master, id, t);
	else {
		alert('没有下一条了.');
		$('#next').css('display', 'none');
	}
}
function toJSON(str) {
	str = str.replace(/^{*|}*$/g, '');
	var attrs = str.split(', '), p = {};
	attrs.forEach(function(property) {
	    var t = property.split('=');
	    p[t[0]] = t[1];
	});
	return p;
}
$(document).ready(function() {
    $.ajax({
        url: basePath + 'common/cross.action',
        type: 'GET',
        data: {
            path: 'http://218.17.158.219:8001/artifactory/libs-release-local/com/uas/android/pm/maven-metadata.xml'
        },
        dataType: 'xml',
        success: function(data) {
            if (data) {
            	// latest version
                var version = $(data).find("metadata").find("versioning").find("latest").text();
                $('#download_client').attr('href', '//218.17.158.219:8001/artifactory/libs-release-local/com/uas/android/pm/' + version + '/pm-' + version + '.apk');
            }
        }
    });
});