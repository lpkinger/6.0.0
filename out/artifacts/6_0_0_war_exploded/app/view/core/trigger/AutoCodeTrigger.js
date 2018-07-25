/**
 * 自动获取编号的trigger
 */
Ext.define('erp.view.core.trigger.AutoCodeTrigger', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.autocodetrigger',
	triggerCls : 'x-form-autocode-trigger',
	afterrender : function() {
		this.addEvent({
					'aftertrigger' : true
				});
	},
	onTriggerClick : function(e) {
		if(("PreProduct" == caller && (this.id == 'pre_uuid' || this.id =='pre_orispeccode'))||("Product" == caller && (this.id == 'pr_uuid'||this.id == 'pr_orispeccode'))){
			this.showUUWin();
		}else if("ProductKind"==caller && this.id == 'pk_selfkind'){
			this.showKindWin();
		}else{
			if ("PreProduct" == caller) {
				var k1 = Ext.getCmp('pre_kind'), k2 = Ext.getCmp('pre_kind2'), k3 = Ext
						.getCmp('pre_kind3');
				if (k1 && !Ext.isEmpty(k1.getValue()) && k2
						&& !Ext.isEmpty(k2.getValue()) && k3
						&& !Ext.isEmpty(k3.getValue())) {
					var k4 = Ext.getCmp('pre_xikind');
					this.askFor(k1.getValue(), k2.getValue(), k3.getValue(),
							(k4 && !Ext.isEmpty(k4.getValue()))
									? k4.getValue()
									: null);
					return;
				}
			}
			if ("FeePlease!YZSYSQ" == caller && Ext.getCmp('fp_statuscode') && Ext.getCmp('fp_statuscode').value == 'COMMITED') {
				var k1 = Ext.getCmp('FP_V4');
				if (k1 && !Ext.isEmpty(k1.getValue())) {
					var k2 = Ext.getCmp('FP_V5'),k3 = Ext.getCmp('FP_V7'), k4 = Ext.getCmp('FP_V8');
					this.askFor(k1.getValue(), 
							(k2 && !Ext.isEmpty(k2.getValue()))? k2.getValue(): null, 
							(k3 && !Ext.isEmpty(k3.getValue()))? k3.getValue(): null,
							(k4 && !Ext.isEmpty(k4.getValue()))? k4.getValue(): null);
					return;
				}
			}
			this.showWin();	
		}
	},
	showWin : function() {
//		var win = this.win;
		var win = this.up('form').win;
		if (!win) {
			var type = this.type || this.getType();
			var trigger=this.id;
			var status=Ext.getCmp('fp_statuscode')?Ext.getCmp('fp_statuscode').getValue():'';
//			this.win = win = new Ext.window.Window({
			this.up('form').win = win = new Ext.window.Window({
				id : 'win',
				height : "100%",
				width : "80%",
				maximizable : true,
				closeAction : 'hide',
				buttonAlign : 'center',
				layout : 'anchor',
				title : '获取编号',
				items : [{
					tag : 'iframe',
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe" src="'
							+ basePath
							+ 'jsps/scm/product/autoGetNum.jsp?type='
							+ type+'&trigger='+trigger+'&status='+status
							+ '" height="100%" width="100%" frameborder="0"></iframe>'
				}]
			});
		}
		win.show();
	},
	askFor : function(k1, k2, k3, k4) {
		var me = this, s = '大类:' + k1 + (k2 ? (';中类:' + k2) : '') + (k3 ? (';小类:' + k3) : '')
				+ (k4 ? (';细类:' + k4) : '');
		var box = Ext.create('Ext.window.MessageBox', {
					buttonAlign : 'center',
					buttons : [{
								text : '生成编号',
								handler : function(b) {
									me.autoCode(k1, k2, k3, k4);
									b.ownerCt.ownerCt.close();
								}
							}, {
								text : '重新选择',
								handler : function(b) {
									me.showWin();
									b.ownerCt.ownerCt.close();
								}
							}, {
								text : '关闭',
								handler : function(b) {
									b.ownerCt.ownerCt.close();
								}
							}]
				});
		box.show({
					title : "提示",
					msg : "您已选择了【" + s + '】<br>现在立刻生成编号?',
					icon : Ext.MessageBox.QUESTION
				});
	},
	autoCode : function(k1, k2, k3, k4) {
		var me = this;
		var codepostfix = me.getCodePostfix(caller);
		var postfix = '';
		if(codepostfix != null && codepostfix != ''){
			var codes = new Array();
			if(codepostfix.indexOf('+')>0){
				codes = codepostfix.split('+');
				for(var i=0;i<codes.length;i++){
					if(!Ext.getCmp(''+codes[i]+'')){
						showError("设置的后缀码字段不存在或者没有勾选或者没有按照特定格式输入,请确认!");
						return;
					}else{
						postfix = postfix +  Ext.getCmp(''+codes[i]+'').value ;
					}
				}
			}else{
				postfix =  Ext.getCmp(''+codepostfix+'').value ;
			}
		}
		Ext.Ajax.request({
					url : basePath + me.getUrl(),
					params : {
						k1 : k1,
						k2 : k2,
						k3 : k3,
						k4 : k4,
						postfix : postfix
					},
					method : 'post',
					callback : function(opt, s, res) {
						var r = new Ext.decode(res.responseText);
						if (r.exceptionInfo) {
							showError(r.exceptionInfo);
						} else if (r.success && r.number) {
							if ("FeePlease!YZSYSQ" == caller){
								var code=r.number.split(";")[0];
								var len=r.number.split(";")[1];
								Ext.getCmp('FP_V13').setValue(code);														
								Ext.getCmp('FP_V12').setValue(code.substr(0,(code.length-len)));		  
								Ext.getCmp('FP_N2').setValue(len);
								Ext.getCmp('FP_N1').setValue(code.substr((code.length-len),code.length));								
							}
							var pr_piccode = Ext.getCmp('pr_piccode');console.log(r.number);
							if(pr_piccode&&pr_piccode.value!=''){
								if(r.number!=null && r.number != "null"){
									if ("PreProduct" == caller) {
										me.setValue(r.number+pr_piccode.value);
										me.autoSave(r.number+pr_piccode.value);
									}
									if ("Product" == caller ) {
										me.setValue(r.number+pr_piccode.value);
									}
								}
							}else{
								if(r.number!=null && r.number != "null"){
									if ("PreProduct" == caller) {
										me.setValue(r.number);
										me.autoSave(r.number);
									}
									if ("Product" == caller ) {
										me.setValue(r.number);
									}
								}
							}
						} else {
							alert('取号失败!');
						}
					}
				});
	},
	autoSave : function(num) {
		var id = this.up('form').down('#pre_id');
		if (id && id.value > 0) {
			Ext.Ajax.request({
						url : basePath + 'common/updateByCondition.action',
						params : {
							table : 'PreProduct',
							update : 'pre_code=\'' + num + '\'',
							condition : 'pre_id=' + id.value
						},
						method : 'post',
						callback : function(opt, s, res) {
							var r = new Ext.decode(res.responseText);
							if (r.exceptionInfo) {
								showError('编号保存失败.<br>' + r.exceptionInfo);
							}
						}
					});
		}
	},
	getType : function() {
		var type = 'Product';
		switch (caller) {
			case 'PreProduct' :
				type = 'Product';
				break;
			case 'Vendor' :
				type = 'Vendor';
				break;
			case 'PreVendor' :
				type = 'Vendor';
				break;
			case 'Customer' :
				type = 'Customer';
				break;
			case 'PreCustomer' :
				type = 'Customer';
				break;
			case 'FeePlease!YZSYSQ' :
				type = 'FeePlease!YZSYSQ';
				break;
		}
		return type;
	},
	getUrl : function() {
		var type = this.getType();
		var url = 'scm/product/getProductKindNum.action';
		switch (type) {
			case 'Vendor' :
				url = 'scm/purchase/getVendorKindNum.action';
				break;
			case 'Customer' :
				url = 'scm/sale/getCustomerKindNum.action';
				break;
			case 'FeePlease!YZSYSQ' :
				url = 'oa/fee/getContractTypeNum.action';
				break;
		}
		return url;
	},
	showUUWin : function() {
		var uuWin = this.uuWin;
		if (!uuWin) {	
			var status= '';
			this.uuWin = uuWin = new Ext.window.Window({
				id : 'uuWin',
				height : "100%",
				width : "80%",
				maximizable : true,
				closeAction : 'hide',
				buttonAlign : 'center',
				layout : 'anchor',
				title : '获取标准型号',
				items : [{
					tag : 'iframe',
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe" src="'
							+ basePath
							+ 'jsps/scm/product/getUUid.jsp?type='
							+ caller+'&status='+status
							+ '" height="100%" width="100%" frameborder="0"></iframe>'
				}]
			});
		}
		uuWin.show();
	},
	showKindWin : function() {
		var KindWin = this.KindWin;
		if (!KindWin) {	
			var status= '';
			this.KindWin = KindWin = new Ext.window.Window({
				id : 'kindWin',
				height : "100%",
				width : "80%",
				maximizable : true,
				closeAction : 'hide',
				buttonAlign : 'center',
				layout : 'anchor',
				title : '获取器件类目',
				items : [{
					tag : 'iframe',
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe" src="'
							+ basePath
							+ 'jsps/scm/product/getKind.jsp?type='
							+ caller+'&status='+status
							+ '" height="100%" width="100%" frameborder="0"></iframe>'
				}]
			});
		}
		KindWin.show();
	},
	getCodePostfix:function(caller){
    	var code = '';
    	Ext.Ajax.request({
	   		url : basePath + 'scm/product/getCodePostfix.action',
	   		async: false,
	   		params:
	   		{
	   			caller : caller
	   		},     					   		
	   		method : 'post',
	   		callback : function(options,success,response){   					   			
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				code = localJson.code;
    			}
	   		}
		});
    	return code;
    }
});