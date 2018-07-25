Ext.define("erp.view.core.button.BatchTransfer",{
	extend: 'Ext.Button', 
	alias: 'widget.erpBatchTransferButton',
	iconCls: 'x-button-icon-submit',
	cls: 'x-btn-gray',
    id: 'batchtransferbtn',
	text: $I18N.common.button.erpBatchTransferButton,
	style: {
		marginLeft: '10px'
    },
    FormUtil: Ext.create('erp.util.FormUtil'),
    width: 140,
	initComponent : function(){ 
		this.callParent(arguments); 
	},	
	listeners:{
	click:function(btn){
		var banktype=Ext.getCmp("tr_banktype");
		if(banktype){
			var type=banktype.displayTplData[0].display||banktype.value;
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
	createWin:function(flag,bank){
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
						me.mainprocess(flag,bank);
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
	check:function(models){
		var arr=new Array(),me=this,str;
		Ext.each(models,function(model,index){
			var data=model.data;
			data.verified=true;
			if(!data.tr_kind||!data.tr_amount||!data.tr_bankaccount||!data.tr_bankname||!data.tr_accountname){
				data.verified=false;
				arr.push(data.tr_code);
			}
		});
		if(arr.length>0){
			str=Ext.encode(arr.join(","));
			showError('必填项未填写!单号:<br/>'+str);
			return false;
		}		
		return true;
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
			la_psw.reset();
		}
		
		var grid=Ext.getCmp('batchDealGridPanel');
		var models=grid.getSelectionModel().getSelection();
		var s1=me.check(models);
		if(!s1)return;
		if(models.length>0){
			Ext.each(models,function(model,index){
				var data=model.data;		
				obj=new Object();
				obj.code=data.tr_code;
				obj.class_=data.tr_kind;
				obj.amount=data.tr_amount;
				obj.purpose=data.tr_purpose;
				if(data.tr_amount.toString().indexOf(".")>=0){
				if(data.tr_amount.toString().split(".")[1].length>2){
					flag=false;
					showError('付款金额小数部分不能多于2位  单号:'+data.tr_code);
					return flag;
				}
				}
				arr.push(obj);
			});
		if(!flag)return;
		}else{
			showError("请勾选明细行");
			return;
		}		
		me.FormUtil.setLoading(true);
		var total=arr.length;
		var jsonArr=unescape(escape(JSON.stringify(arr)));
		Ext.Ajax.request({
			url:basePath+'fa/api/'+bank+'/batchTransfer.action',
			params:{
			data:jsonArr,			
			password:psw?psw:'',
			},
			timeout:6*60*1000,
			method:'post',
			callback:function(opts,success,res){
				me.FormUtil.setLoading(false);
				var res=Ext.decode(res.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);
					return;
				}
				if(res.result){
					var success=res.success,err;
					err=total==success?"":":失败详情见<font color='red'>上传信息反馈</font>字段"	;				
					showMessage("提示",'上传成功'+success+'笔'+"<br/>上传失败"+(total-success)+"笔"+err);			
					grid.tempStore={};//操作成功后清空暂存区数据   				
					//Ext.Msg.alert("提示", "上传成功");
					/*var total=res.total,failed=res.failed,beyondAmount,wrongBankName,payPart,error=null;
					if(res.beyondAmount)beyondAmount=res.beyondAmount;					
					if(res.wrongBankName)wrongBankName=res.wrongBankName;
					if(res.payPart)payPart=res.payPart;
					if(failed==0){
					showMessage("提示", "上传成功"+(total-failed)+'条'+';  上传失败'+failed+'条'); 
					}else{
						if(beyondAmount)error="<br/>付款金额超出剩余金额且不为负数！单号:"+beyondAmount;
						if(wrongBankName)error=error==null?""+"<br/>开户行名不正确！单号:"+wrongBankName:error+"<br/>开户行名不正确！单号:"+wrongBankName;
						if(payPart)error=error==null?""+"<br/>费用报销/借款申请不允许部分付款！单号："+payPart:error+"<br/>费用报销/借款申请不允许部分付款！单号："+payPart;
						showMessage("提示", "上传成功"+(total-failed)+'条'+';  上传失败'+failed+'条: '+error); 
					}*/					
					grid.multiselected = new Array();
					Ext.getCmp('dealform').onQuery();
				}
			}
		});
	}
});
		