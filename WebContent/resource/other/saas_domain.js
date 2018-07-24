(function($) {
	document.onkeydown = function(e) {
		var ev = document.all ? window.event : e;
		if (ev.keyCode == 13) {
			searchDomain();
		}
	}
})(jQuery);
function searchDomain() {
	var key = $('#search').val();
	if (!key || key.trim().length == 0)
		return;
	$.ajax({
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		url : "search.action?key=" + key,
		success : function(c) {
			if (c) {
				var html = '<ul class="list-unstyled">';
				$.each(c, function(i, master){
					html += '<li><a class="text-link" href="' + master.ma_url + '">' + parseMasterName(master.ma_function, key) + '</a><br><a class="text-muted" href="' + master.ma_url + '">' + master.ma_url + '</a></li>';
				});
				html += '</ul>';
				$('#search-result').html(html);
				$('#result-count').text(c.length);
				$('#result-wrap').show();
			}
		}
	});
}
function parseMasterName(name, key) {
	return name.replace(key, '<span class="text-inverse">' + key + '</span>');
}