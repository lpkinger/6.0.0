Ext.define("erp.view.core.button.BatchSearch",{
	extend: 'Ext.Button', 
	alias: 'widget.erpBatchSearchButton',
	iconCls: 'x-button-icon-submit',
	cls: 'x-btn-gray',
	id: 'batchsearchbtn',
	FormUtil:Ext.create('erp.util.FormUtil'),
	//BaseUtil: Ext.create('erp.util.BaseUtil'),
	text: $I18N.common.button.erpBatchSearchButton,
	style: {
		marginLeft: '10px'
    },
    width: 100,
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	listeners:{
	click:function(btn){
		var banktype=Ext.getCmp("banktype");
		if(banktype){
			var type=banktype.displayTplData[0].display;
			if(type=='民生银行'){
				this.createWin(true,'cmbc');
			}else if(type=='浦发银行'){
				this.mainprocess(false,'spdb');
			}else{
				showError("请先选择转账银行!");
				return;
			}
		}else{
			showError("请先选择转账银行!");
			return;
		}
	}
	},
	createWin:function(f,bank){
		var me = this, win = me.querywin;
		if (!win) {
			var form  = me.createForm();
			win = me.querywin = Ext.create('Ext.window.Window', {
				closeAction : 'hide',
				title : '安全验证',
				height: 150,
        		width: 350,
        		layout: 'border',
				items : [form],
				buttonAlign : 'center',
				listeners:{
					hide:function(){
						var la_psw=Ext.getCmp("psw");
						la_psw.reset();
					}
				},
				buttons : [{
					text : '确认',
					height : 26,
					iconCls: 'x-button-icon-check',
					handler : function(btn) {
						me.mainprocess(f,bank);
						btn.ownerCt.ownerCt.hide();
					}
				},{
					text : '取消',
					iconCls: 'x-button-icon-close',
					height : 26,
					handler : function(b) {						
						b.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
	},
	createForm:function(){
    	var me = this;
    	var form = Ext.create('Ext.form.Panel', {
    		region: 'center',
    		id:'check-form',
    		anchor: '100% 100%',
    		layout: 'column',
    		autoScroll: true,
    		items:[{
    			columnWidth: 0.9,
    	    	xtype: 'textfield',
    	    	name: 'password',
    	    	id:"psw",
    	    	fieldCls: 'x-form-field-cir',
    	    	labelWidth: 130,
    	    	labelStyle: 'font-family:隶书;font-size:18px;color:#473C8B;',
    	    	fieldLabel: '安全验证码',
    	    	inputType: 'password',
    	    	labelAlign : "right",
    		}],
    		defaults: {
    			columnWidth: 1,
    			margin: '25 8 16 0'
    		},
    		bodyStyle: 'background:#f1f2f5;',
    	});
    	return form;   
	},
	mainprocess:function(f,bank){
	var arr=new Array(),obj,flag=true,err,me=this,psw;
	if(f){
		var la_psw=Ext.getCmp("psw");
		psw=la_psw.value;
		if(!psw){
			showError("请输入相应的安全验证数据!");
			return;
		}
	}
	
	var arr=new Array();
	var grid=Ext.getCmp('batchDealGridPanel');
	var models=grid.getSelectionModel().getSelection();		
	if(models.length>0){
		Ext.each(models,function(model,index){			
			var data=model.data,id;
			id=data.id_;
			arr.push(id);
		});
	}else{
		showError("请勾选明细行");
		return;
	}	
	var jsonArr=Ext.encode(arr);
	me.FormUtil.setLoading(true);
	Ext.Ajax.request({
		url:basePath+'/fa/api/'+bank+'/batchSearch.action',
		params:{
		ids:jsonArr,
		password:psw
		},
		timeout:6*60*1000,
		method:'post',
		callback:function(opts,success,res){
			me.FormUtil.setLoading(false);
			var res=Ext.decode(res.responseText);
				if(res.exceptionInfo != null){
				showError(res.exceptionInfo);return;
			}
				if(res.result){
					grid.tempStore={};//操作成功后清空暂存区数据   		
					//Ext.Msg.alert("提示", "查询成功");
					showMessage("提示", "查询完成");    				
					grid.multiselected = new Array();
					Ext.getCmp('dealform').onQuery();					
				}
				}			
	});
	}
});