Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.AutoGetNum', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.product.ProductKindTree','scm.product.AutoGetNum'
    	],
    init:function(){
    	var me = this;
    	me.lastSelected = null;
    	me.lastCode = '';
    	me.codeisnull = true;
    	this.control({ 
    		'prodkindtree': {
    			itemmousedown: function(selModel, record){
    				me.loadTab(selModel, record);
    				me.lastSelected = record;
    			},
    			itemdblclick: function(view, record){
    				me.lastSelected = record;
    				var btn = Ext.getCmp('confirm');
    				btn.fireEvent('click', btn);
    			}
    		},
    		'button[name=code]': {
    			afterrender: function(btn) {
    				btn.hide();
    				//允许在新物料申请的时候按物料类型生成料号
    				me.BaseUtil.getSetting(parent.window.caller, 'getCodeByKind', function(bool) {
						if(bool) {
							btn.show();
						}
	                });	
	                 if(type=='FeePlease!YZSYSQ' && status=='COMMITED'){
	                  btn.show();              
	                 }
    			},
    			click: function(btn){
    				if(me.lastSelected != null){
    					if(me.lastSelected.isLeaf() || me.lastSelected.childNodes.length == 0){
    						var pr_piccode = parent.Ext.getCmp('pr_piccode');
    						var codepostfix = me.getCodePostfix(parent.caller);
    						var postfix = '';
    						if(codepostfix != null && codepostfix != ''){
    							var codes = new Array();
    							if(codepostfix.indexOf('+')>0){
    								codes = codepostfix.split('+');
	    							for(var i=0;i<codes.length;i++){
	    								if(!parent.Ext.getCmp(''+codes[i]+'')){
	    									showError("设置的后缀码字段不存在或者没有勾选或者没有按照特定格式输入,请确认!");
	    									return;
	    								}else{
	    									postfix = postfix +  parent.Ext.getCmp(''+codes[i]+'').value ;
	    								}
	    							}
    							}else{
    								postfix =  parent.Ext.getCmp(''+codepostfix+'').value ;
    							}
    						}
    						Ext.Ajax.request({
    					   		url : basePath + me.getUrl(),
    					   		params:
    					   		{
    					   			id: me.lastSelected.data['id'],	
    					   			table:me.lastCode,
    					   			postfix : postfix
    					   		},     					   		
    					   		method : 'post',
    					   		callback : function(options,success,response){   					   			
    					   			var localJson = new Ext.decode(response.responseText);
    					   			if(localJson.exceptionInfo){
    					   				showError(localJson.exceptionInfo);
    					   			}
    				    			if(localJson.success){
    				    				var auto_code = Ext.getCmp('auto_code');
    				    				if(localJson.number!=null && localJson.number != "null"){
    				    					if(pr_piccode&&pr_piccode.value!=''){
    				    						auto_code.setValue(me.lastCode + localJson.number + pr_piccode.value.replace(/(^\s*)|(\s*$)/g, ""));
        				    				}else{
        				    					auto_code.setValue(me.lastCode + localJson.number);
        				    				}
    				    				}else{
    				    					if(pr_piccode&&pr_piccode.value!=''){
    				    						auto_code.setValue(me.lastCode + pr_piccode.value.replace(/(^\s*)|(\s*$)/g, ""));
        				    				}else{
        				    					auto_code.setValue(me.lastCode);
        				    				}
    				    				}
    				    				me.codeisnull = false;
    				    				if(!me.codeisnull){
    				    					var f = parent.Ext.ComponentQuery.query('autocodetrigger');
    				    					if(f){
    				    						if(me.lastSelected != null){
    				    							var s = me.lastSelected.getPath('text', ';').split(';'),
    				    								arr = new Array();
    				    							Ext.each(s, function(){
    				    								if(this != '' && this != 'root') {
    				    									arr.push(String(this));
    				    								}
    				    							});
    				    							var val = auto_code.value;  
    				    							f[0]=parent.Ext.getCmp(trigger);
    				        						f[0].setValue(val);   				        					
    				        						f[0].fireEvent('aftertrigger', f[0], val, arr, me.lastSelected);
    				    						}
    				    					}
    				    					parent.Ext.getCmp('win').close();
    				    				} else {
    				    					showError("物料编号还没有生成!");
    				    				}
    				    			}
    					   		}
    						});
        				} else {
        					showError("[" + me.lastSelected.data['text'] + "]下面还有详细种类!");
        				}
    				} else {
    					showError("请选择种类!");
    				}
    			}
    		},
    		'button[name=confirm]':{   
    			afterrender: function(btn) {   				
	                 if(type=='FeePlease!YZSYSQ' && status=='COMMITED'){
	                  btn.hide();              
	                 }
    			},
    			click:function(){
    				var f = parent.Ext.ComponentQuery.query('autocodetrigger');
					if(f){
						if(me.lastSelected != null){
							var s = me.lastSelected.getPath('text', ';').split(';'),
								arr = new Array();
							Ext.each(s, function(){
								if(this != '' && this != 'root') {
									arr.push(this);
								}
							});
							parent.Ext.getCmp('win').close();
    						f[0].fireEvent('aftertrigger', f[0], null, arr, me.lastSelected);
						}
					}
    			}
    		},
    		'button[name=close]': {
    			click: function(){
    				parent.Ext.getCmp('win').close();
    			}
    		}
    	});
    },
    loadTab: function(selModel, record){
    	var me = this;
    	var tree = Ext.getCmp('tree-panel');
    	var parentId='';
    	if (record.get('leaf')) {
    		parentId=record.data['parentId'];
    	} else {
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
		            tree.setLoading(true, tree.body);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + tree.getUrl(),
			        	params: {
			        		parentid: record.data['id'],
			        		allKind:this.allKind
			        	},
			        	async: false,
			        	callback : function(options,success,response){
			        		tree.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			record.appendChild(res.tree);
			        			record.expand(false,true);//展开
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false,true);//展开
				}
			}
    	}
    	tree.getExpandedItems(record);
    	Ext.each(tree.expandedNodes, function(){
    		if(!this.data['leaf'] && this.data['parentId']==parentId )
    		this.collapse(true,true);
    	});
    	me.lastCode = '';
    	var choose = new Array();
    	tree.getExpandedItems(record);
    	Ext.each(tree.expandedNodes, function(){
    		me.lastCode += this.data['qtip'];		
    		choose.push(this.data['text']);
    	}); 	
    	var c = Ext.getCmp('choose');
    	c.show();
    	c.update({nodes: choose});
    	Ext.getCmp('auto_code').setValue(me.lastCode);
    	me.codeisnull = true;
    },
    getUrl: function(){
    	type = type || 'Product';
    	var url = 'scm/product/getProductKindNum.action';
    	switch (type) {
	    	case 'Vendor':
	    		url = 'scm/purchase/getVendorKindNum.action';break;
	    	case 'Customer':
	    		url = 'scm/sale/getCustomerKindNum.action';break;
	    	case 'FeePlease!YZSYSQ':
	    		url = 'oa/fee/getContractTypeNum.action?';break;
    	}
    	return url;
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
    },
	getSetting : function(type, fn) {
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'Setting',
	   			field: 'se_value',
	   			condition: 'se_what=\'' + type + '\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success && r.data){
	   				fn.call(null, r.data == 'true');
	   			}
	   		}
		});
	}
});