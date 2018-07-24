//dbFind模态框效果
var dbFind = function(config, callback){
	config.listener && config.listener.click(function() {
		$('#dbFindModal').modal('show');
		$('#myModalLabel').text(config.header);
		callback && callback();
	});
};