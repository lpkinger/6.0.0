Ext.define('erp.view.fs.credit.CustCreditTargetsForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpCustCreditTargetsFormPanel',
	id: 'custCreditTargetsForm', 
	keyField:'custCreditTargetsForm',
    frame : true,
    header: false,//不显示title
	tbar:null,
	value:'',
	style:'border-width:0 0 0 0;padding:0px;',
	initComponent : function(){
		this.callParent(arguments);
		this.getDisplay();
	},
	getDisplay:function(){
		var me = this;
		Ext.Ajax.request({
			url:basePath + 'fs/credit/getDisplay.action',
			params:{
				caller:pCaller,
				craid:craid,
				type:type
			},
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					me.html = res.display;				
					if(res.display.indexOf('</b> ')>0){
						var year = res.display.substr(res.display.indexOf('</b> ')+5,4); 		
						me.value = year;
					}
					
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});
	}
});