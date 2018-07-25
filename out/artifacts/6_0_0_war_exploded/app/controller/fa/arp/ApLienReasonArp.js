Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.ApLienReasonArp', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.grid.Panel4','core.toolbar.Toolbar3','core.button.Save','core.button.Delete','core.trigger.CateTreeDbfindTrigger','core.grid.YnColumn'
      	
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel4': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.beforeUpdate();

    			}
    		},'erpDeleteButton': {
    			click: function(btn){
    				this.GridUtil.deleteDetailForEditGrid(btn);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClickForEditGrid(selModel, record);
    },

	beforeUpdate: function(){

		var mm = this;
		var s2 = '';
		var grids = Ext.ComponentQuery.query('gridpanel');
		if(grids.length > 0){//check所有grid是否已修改
			Ext.each(grids, function(grid, index){
				var msg = grid.GridUtil.checkGridDirty(grid);
				if(msg.length > 0){
					s2 = s2 + '<br/>' + grid.GridUtil.checkGridDirty(grid);
				}
			});
		}
			var params = [];
			if(grids.length > 0){
				var param = grids[0].GridUtil.getGridStore();
				if(grids[0].necessaryField.length > 0 && (param == null || param == '')){
					warnMsg('明细表还未添加数据,是否继续?', function(btn){
						if(btn == 'yes'){
							params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
							mm.update(params);
						} else {
							return;
						}
					});
				} else {
					params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
					mm.update(params);
				}
			}
//			mm.update(params);

		},
	update:function(){

		var params = new Object();
//		var r = arguments[0];
//		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[0].toString().replace(/\\/g,"%"));
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
		}
		var mm = this;

		Ext.Ajax.request({
	   		url : basePath+'fa/saveApLienReasonArp.action',
	   		caller:caller,
	   		params: params,
	   		method : 'post',
	   		callback : function(options,success,response){
//	   			mm.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
//    				Ext.Msg.alert('保存成功');
    				
    				updateSuccess(function(btn){
    					//update成功后刷新页面进入可编辑的页面 
    					var url = window.location.href;
//    					var value = r[form.keyField];
/*		   		    	var formCondition = '';
		   		    	var gridCondition = '';
		   		    	var grid = Ext.getCmp('grid');
		   		    	if(grid && grid.mainField){
		   		    		gridCondition = grid.mainField + "IS" + value;
		   		    	}
		   		    	if(mm.contains(url, 'formCondition', true)){
		   		    		url = url.replace('formCondition', 1);
		   		    		url = url.replace('gridCondition', 1);
		   		    	}
		   		    	if(mm.contains(url, '?', true)){
		   		    		url = url + '&formCondition=' + 
		   						formCondition + '&gridCondition=' + gridCondition;
			   		    } else {
			   		    	url = url + '?formCondition=' + 
		   						formCondition + '&gridCondition=' + gridCondition;
			   		    }*/
		   		    	window.location.href = url;
	   				});
	   			} else if(localJson.exceptionInfo){/*
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					updateSuccess(function(btn){
	    					//update成功后刷新页面进入可编辑的页面 
	    					var url = window.location.href;
	    					var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value;
			   		    	var gridCondition = '';
			   		    	var grid = Ext.getCmp('grid');
			   		    	if(grid && grid.mainField){
			   		    		gridCondition = grid.mainField + "IS" + value;
			   		    	}
			   		    	if(mm.contains(url, 'formCondition', true)){
			   		    		url = url.replace('formCondition', 1);
			   		    		url = url.replace('gridCondition', 1);
			   		    	}
			   		    	if(mm.contains(url, '?', true)){
			   		    		url = url + '&formCondition=' + 
			   						formCondition + '&gridCondition=' + gridCondition;
				   		    } else {
				   		    	url = url + '?formCondition=' + 
			   						formCondition + '&gridCondition=' + gridCondition;
				   		    }
			   		    	window.location.href = url;
		   				});
	   				}
        			showError(str);return;
        		*/} else {
	   				updateFailure();
	   			}
	   		}
		});
	
	},

    /**
	 * string:原始字符串
	 * substr:子字符串
	 * isIgnoreCase:忽略大小写
	 */
	contains: function(string,substr,isIgnoreCase){
	    if(isIgnoreCase){
	    	string=string.toLowerCase();
	    	substr=substr.toLowerCase();
	    }
	    var startChar=substr.substring(0,1);
	    var strLen=substr.length;
	    for(var j=0;j<string.length-strLen+1;j++){
	    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
	    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	}
		
});