/**
 * BOM附件批量下载按钮
 */
Ext.define('erp.view.core.button.BOMAttachDownload', {
	extend : 'Ext.Button',
	alias : 'widget.ertBOMAttachDownloadButton',
	text : $I18N.common.button.ertBOMAttachDownloadButton,
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	handler : function(btn) {
		var records = this.getSelectedRecords();
		var ids = '';
		if(records.length>0){		
			Ext.each(records, function(r, index){
				if(r.data['pr_attach'] != null && r.data['pr_attach'] != ''){
					ids += r.data['pr_attach'];
				}
			});
		}
      	if(ids!=''){
           	ids = ids.substring(0,ids.length-1);
           	 if (!Ext.fly('ext-attach-download')) {  
			   var frm = document.createElement('form');  
			   frm.id = 'ext-attach-download';  
			   frm.name = id;  
			   frm.className = 'x-hidden';
			   document.body.appendChild(frm);  
		   	}
           	Ext.Ajax.request({
			   	url: basePath + 'pm/bom/BOMAttachDownload.action',
			   	method: 'post',
			   	form: Ext.fly('ext-attach-download'),
			   	isUpload:true,
			   	params: {
				   	ids:ids,
				   	caller:caller,
				   	bo_mothercode:Ext.getCmp("pr_code").getValue()
			   	},
			   	callback : function(options, success, response){
					if (!response) return;
					var restext=response.responseText;
					var restext1 = restext.substring(restext.indexOf('{'),restext.indexOf('}') + 1);
					var res =new Ext.decode(restext1);
					if(res.exceptionInfo){
						showError(res.exceptionInfo);					
					}		    							
			   	}
		   	});
       	}else{
       		showError("请选择存在附件的明细后进行再点击此按钮！");
       	}
	},
/*	getSelectedRecords: function(){
    	var grid = Ext.getCmp('batchPrintGridPanel');
        var items = grid.selModel.getSelection();
        if(items.length>0){
	        return items;
	    }else{
	    	showError("请选择明细后进行再点击此按钮！");
	    }
	}*/
	getSelectedRecords: function(){
		//通过层级获取到grid表格
		var grid = this.ownerCt.ownerCt.nextSibling();
        var items = grid.selModel.getSelection();
        if(items.length>0){
	        return items;
	    }else{
	    	showError("请选择明细后进行再点击此按钮！");
	    }
	}
});