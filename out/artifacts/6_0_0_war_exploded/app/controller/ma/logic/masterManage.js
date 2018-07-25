Ext.QuickTips.init();
Ext.define('erp.controller.ma.logic.masterManage', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['ma.logic.masterManage','core.toolbar.Toolbar','core.button.Confirm','core.button.Close'],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		 'erpConfirmButton':{
    			 click:function(btn){ 
    				var grid=Ext.getCmp('mastergrid');
    				param=me.getParam(grid);
    				if(param==""){
    					showError("未进行任何操作");
    					return ;
    				}
    				Ext.Ajax.request({
    			   		url :basePath+ 'ma/logic/setMasterInfo.action',
    			   		params : {
    			   			param: unescape(param.toString().replace(/\\/g,"%")),
    			   			caller:'masterManage'
    			   		},
    			   		callback:function(options,success,response){
    			   			var res = new Ext.decode(response.responseText);
    			   			if(res.exceptionInfo){
    			   				showError(res.exceptionInfo);
    			   			}else{
    			   				window.location=basePath+'jsps/ma/logic/masterManage.jsp';
    			   				showError("操作成功");
    			   			}
    			   		}
    				});
    			 }
    		 }
    	});
    },
    getParam:function(grid){
		 var s = grid.getStore().data.items;
		jsonGridData = new Array();
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			dd = new Object();
			if(s[i].dirty){
				Ext.each(grid.columns, function(c){
					if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
						if(c.xtype == 'datecolumn'){
							c.format = c.format || 'Y-m-d';
							if(Ext.isDate(data[c.dataIndex])){
								dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
							} else {
								if(c.editor&&c.logic!='unauto'){
									dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
								}else  dd[c.dataIndex]=null;
							}
						} else if(c.xtype == 'datetimecolumn'){
							if(Ext.isDate(data[c.dataIndex])){
								dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
							} else {
								if(c.editor&&c.logic!='unauto'){
									dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
								}
							}
						} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
							if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
								dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
							} else {
								dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
							}
						} else {
							dd[c.dataIndex] = s[i].data[c.dataIndex];
						}
						if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
							dd[c.dataIndex] = c.defaultValue;
						}
					}
				});
				if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
					dd[grid.mainField] = Ext.getCmp(form.keyField).value;
				}
				jsonGridData.push(Ext.JSON.encode(dd));
			}
			}
		return jsonGridData;
	 }
});