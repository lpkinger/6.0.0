Ext.QuickTips.init();
Ext.define('erp.controller.ma.group.AutoSync', {
    extend: 'Ext.app.Controller',
    views: ['ma.group.AutoSync'],
    requires: ['erp.util.BaseUtil'],
    refs : [ {
		ref : 'grid',
		selector : 'grid'
	}],
    init:function(){
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'grid': {
    			afterrender: function() {
    				this.getPostStyleSet();
    			}
    		},
    		'#confirm': {
    			click: function() {
    				this.savePostStyleSet();
    			}
    		},
    		'#close': {
    			click: function() {
    				this.BaseUtil.getActiveTab().close();
    			}
    		}
    	});
    },
    getMasters: function() {
    	var columns = new Array(), fields = new Array();
    	columns.push({dataIndex: 'ps_caller', hidden: true});
    	columns.push({dataIndex: 'ps_table', hidden: true});
    	columns.push({dataIndex: 'ps_keyfield', hidden: true});
		columns.push({text: '待同步资料', dataIndex: 'ps_desc', width: 120, cls: 'x-grid-header-1', align: 'center', tdCls: 'x-grid-cell-special'});
		fields.push({name: 'ps_caller', type: 'string'});
		fields.push({name: 'ps_desc', type: 'string'});
		fields.push({name: 'ps_table', type: 'string'});
		fields.push({name: 'ps_keyfield', type: 'string'});
    	Ext.Ajax.request({
			url: basePath + 'common/getAbleMasters.action',
			method: 'GET',
			async: false,
			callback: function(opt, s, r) {
				if (s) {
					var rs = Ext.decode(r.responseText),
						c = rs.currentMaster;
					for(var i in rs.masters) {
						var s = rs.masters[i];
						if(s.ma_name != c && s.ma_type == 3) {
							columns.push({text: s.ma_name, dataIndex: s.ma_user, cls: 'x-grid-header-1', xtype: 'checkcolumn', width: 90, editor: {
				                xtype: 'checkbox',
				                cls: 'x-grid-checkheader-editor'
				            }});
							fields.push({name: s.ma_user, type: 'bool'});
						}
					}
				}
			}
    	});
    	return {columns: columns, fields: fields};
    },
    getPostStyleSet: function() {
    	var grid = this.getGrid(), tab = this.BaseUtil.getActiveTab(), 
    		args = this.getMasters(), columns = args.columns, fields = args.fields;
    	tab.setLoading(true);
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'PostStyle',
	   			fields: 'ps_caller,ps_desc,ps_autosync,ps_table,ps_keyfield',
	   			condition: '1=1 order by ps_id'
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			tab.setLoading(false);
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success){
    				var data = Ext.decode(r.data), datas = new Array();
    				for(var i in data) {
    					var d = data[i], o = {ps_caller: d.PS_CALLER, ps_desc: d.PS_DESC, ps_table: d.PS_TABLE, ps_keyfield: d.PS_KEYFIELD};
    					if(!Ext.isEmpty(d.PS_AUTOSYNC)) {
    						var as = d.PS_AUTOSYNC.split(',');
    						for (var j in as) {
    							o[as[j]] = true;
    						}
    					}
    					datas.push(o);
    				}
    				grid.reconfigure(Ext.create('Ext.data.Store', {
    					fields: fields,
    					data: datas
    				}), columns);
	   			}
	   		}
		});
    },
    savePostStyleSet: function() {
    	var me = this, grid = this.getGrid(), datas = new Array();
		grid.store.each(function(){
			if(this.dirty) {
				var d = this.data, keys = Ext.Object.getKeys(d), dd = new Array();
				for(var i in keys) {
					var k = keys[i];
					if(k != 'ps_caller' && k != 'ps_desc' && k != 'ps_table'
						&& k != 'ps_keyfield' && d[k]) {
						dd.push(k);
					}
				}
				datas.push({ps_caller: d.ps_caller, ps_autosync: dd.join(','), ps_table: d.ps_table, ps_keyfield: d.ps_keyfield});
			}
		});
		if(datas.length == 0) {
			showError('未修改数据!');return;
		}
		Ext.Ajax.request({
			url: basePath + 'ma/group/updatePostStyleSet.action?caller=' + caller,
			params: {
				data: Ext.encode(datas)
			},
			callback: function(opt, s, res) {
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success){
    				alert('设置成功!');
    				me.getPostStyleSet();
    			}
			}
		});
    }
});