/**
 * 公共按钮 CallProcedureByConfig
 */
Ext.define('erp.view.core.button.CallProcedureByConfig',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCallProcedureByConfig',
		param: [],
		name: 'erpCallProcedureByConfig',
		//text: $I18N.common.button.erpAbateButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	//width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners : {
			beforerender : function(btn){
				//var form = btn.up('form');
				console.log(caller);
				Ext.Ajax.request({
					url: basePath + 'common/getButtonconfigs.action',
					async : false,
					method : 'post',
					params: {
						caller : caller
					},
					callback: function(opt, s, r) {
						if(r){
							var res = new Ext.decode(r.responseText);
							Ext.each(res.log,function(b,index){
								var f = btn.ownerCt;
								var button = f.down("button[name="+b.BC_BUTTONNAME+"]");
								if(button){
									button.text = b.BC_BUTTONDESC;
									button.width = b.BC_WIDTH;
									var condition = b.BC_CONDITION;
									if(condition != null && condition != ''){
										var c = "\\["; // 1.先计算condition中有多少个'['符号；
										var regex = new RegExp(c, 'g'); //使用g表示整个字符串都要匹配，i匹配第一个
										var result = condition.match(regex);
										var count = !result ? 0 : result.length;//2.计算condition中的'['个数
										var	reg = new RegExp('\\[(.+?)\\]',"i");
										var	ss = condition;
										for(var i=0;i<count;i++){//3.然后每次将condition中的第一个[]中的值取出替换；
											var r = reg.exec(ss);console.log(r);
											ss = ss.replace(reg,"'"+Ext.getCmp(r[1]).value+"'");
										}
										if(eval(ss)){
											button.show();
										}else{
											button.hide();
										}
									}
								}
							});
						}
					}
				});
			},
			click : function(btn){
				var form = btn.up('form');
	        	var fo_keyField = form.fo_keyField;
	        	var keyFieldValue = Ext.getCmp(fo_keyField).value;
	        	Ext.Ajax.request({
	        		url : basePath + 'oa/common/turnAllCommon.action',
	        		method : 'post',
					params: {
						caller : caller,
						id : keyFieldValue,
						name : btn.name
					},
					callback: function(opt, s, r) {
						var res = new Ext.decode(r.responseText);
						if(res.success){
							showError(res.log);
							window.location.reload();
						}
						if(res.exceptionInfo){
							showError(res.exceptionInfo);
							return;
						}
					}
	        	});
			}
		}
	});