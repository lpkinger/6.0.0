Ext.define('erp.view.sys.base.EnterprisePortal',{
	extend: 'Ext.form.Panel', 
	alias: 'widget.enterpriseportal',
	id:'enterpriseportal',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	bodyStyle : 'background:#f9f9f9;padding:5px 5px 0',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText,
		fieldStyle: "background:white repeat-x 0 0;border-width: 1px;border-style: solid;"
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	layout:'absolute',
	glyph:'65@My Font Family',
	defaults:{
		xtype:'textfield',
		width: 400,
		margin:'5 5 5 5'
	},
	margin:'0 0 0 0',
/*	buttons: [{
		text: '保存',
		formBind: true, //only enabled once the form is valid
		disabled: true,
		style:'margin-bottom:100px;top:0px;',
		height :20,
		handler: function(btn) {
			var form = this.up('form');
			if (form.isValid()) {
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){			
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}else if(item.xtype=='checkbox'){
						item.dirty=true;
						if(item.checked){
						item.inputValue='1';					
						}else item.inputValue='0';
					}
				});
				form.setLoading(true);
				var formStore= unescape(escape(Ext.JSON.encode(form.getValues())));
				Ext.Ajax.request({//拿到tree数据
					url : basePath + 'ma/logic/updateEnterprise.action',
					params:{
					   formStore:formStore
					},
					method:'post',
					timeout: 360000,
					callback : function(options,success,response){
						var res = new Ext.decode(response.responseText);
						if(res.success){
							form.setLoading(false);
							showResult('提示','企业信息确认成功!');
						}else if(res.exceptionInfo != null){
							form.setLoading(false);
							showResult('提示',res.exceptionInfo+'');return false;
	            		}
					}
				});
			}
		}
	}],*/
	initComponent : function(){
		var me=this;
		this.callParent(arguments);
		this.intData(me);
	},
	intData:function(me){
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'common/loadNewFormStore.action',
			params:{
				caller:'Enterprise',
				condition:'1=1'
			},
			method:'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.data){
					me.setItems(res.data,me);
				} 
			}
		});
	},
	setItems:function(data,form){
		data= new Ext.decode(data);
		var items=[
		     {
		      x:'20%',
		      y: 10,
		      name:'company',
		      xtype:'label',
		      html: '<b>企业信息</br>'	   
		},
		{
			fieldLabel:'ID',
			name:'en_id',
			value:data['en_id'],
			hidden:true
		},
		{
			fieldLabel: '企业UU号',
			name: 'en_uu',
			afterLabelTextTpl: required,
			value:data['en_uu'],
			allowBlank: false,
			readOnly:true,
			fieldStyle:'background-color: #f3f3f3; background-image: none;',
			x:'20%',
			y:35
		},
		{
			fieldLabel: '企业名称',
			name: 'en_name',
			afterLabelTextTpl: required,
			value:data['en_name'],
			allowBlank: false,
			fieldStyle:'background-color: #f3f3f3; background-image: none;',
			readOnly:true,
			x:'20%',
			y:65
		},{
			fieldLabel: '企业简称',
			name:'en_shortname',
			value:data['en_shortname'],
			afterLabelTextTpl: required,
			fieldStyle:'background-color: #f3f3f3; background-image: none;',
			allowBlank: false,
			readOnly:true,
			x:'20%',
			y:95
		},{
			xtype:'combo',
			store:Ext.create("Ext.data.Store",{
				fields:['value'],
				data:[
				      {'value':'制造型'},
				      {'value':'贸易型'},
				      {'value':'方案商'},			     
				     ]
			}),
			fieldLabel: '所属行业',
			name:'en_type',
			value:data['en_type'],
			valueField:'value',
			displayField:'value',
			afterLabelTextTpl: required,
			allowBlank: false,
			x:'20%',
			y:125,
			/*listeners:{
				 focus: function(s){
					 console.log(s);
					 s.setFieldStyle('border-color:blue');
				 },
				 blur: function(s){
					 s.setFieldStyle('background: 0px 0px repeat-x white;border-width: 1px;border-style: solid;width: 100%;');
					 
				 }
			}*/
		},{
			fieldLabel:'企业电话',
			name:'en_tel',
			value:data['en_tel'],
			allowBlank:true,
			/*afterLabelTextTpl: required,*/
			x:'20%',
			y:155
		},{
			fieldLabel:'企业传真',
			name:'en_fax',
			value:data['en_fax'],
			x:'20%',
			y:185
		},{
			fieldLabel:'企业营业执照号',
			name:'en_businesscode',
			value:data['en_businesscode'],
			fieldStyle:'background-color: #f3f3f3; background-image: none;',
			readOnly:true,
			x:'20%',
			y:215
		},{
			fieldLabel:'企业注册地址',
			name:'en_address',
			allowBlank:false,
			afterLabelTextTpl: required,
			value:data['en_address'],
			x:'20%',
			y:245
		},
		/*{
			fieldLabel:'企业网址',
			name:'en_url',
			value:data['en_url'],
			x:'20%',
			y:275
		},*/
		{
			name:'admin',
			x: '20%',
			y: 285,
			xtype:'label',
			html: '<b>管理员信息</br>'	   
			},
		{
			fieldLabel:'管理员姓名',
			name:'en_adminname',
			value:data['en_adminname'],
			x:'20%',
			y:315
		},
		{
			fieldLabel:'管理员手机',
			name:'en_adminphone',
			value:data['en_adminphone'],
			x:'20%',
			y:345
		},
		{
			fieldLabel:'管理员邮箱',
			name:'en_adminemail',
			value:data['en_adminemail'],
			x:'20%',
			y:375
		},
		{
			xtype:'button',
			text:'确认',
			id:'enterprisesave',
			formBind: true, //only enabled once the form is valid
			/*disabled: true,*/
			x:'50%',
			y:480,
			style:{background:'#0092d0'},
			height:30,
			width:90,
			handler: function(btn) {
				var form = this.up('form');
				if (form.isValid()) {
					Ext.each(form.items.items, function(item){
						if(item.xtype == 'numberfield'){			
							if(item.value == null || item.value == ''){
								item.setValue(0);
							}
						}else if(item.xtype=='checkbox'){
							item.dirty=true;
							if(item.checked){
							item.inputValue='1';					
							}else item.inputValue='0';
						}
					});
					form.setLoading(true);
					var formStore= unescape(escape(Ext.JSON.encode(form.getValues())));
					Ext.Ajax.request({//拿到tree数据
						url : basePath + 'ma/logic/SaasupdateEnterprise.action',
						params:{
						   formStore:formStore
						},
						method:'post',
						timeout: 360000,
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.success){
								form.setLoading(false);
								var gris=document.getElementById('progress');
								Ext.Ajax.request({//拿到form的items
				    				url : basePath + "common/saas/common/checkData.action",
				    				params: {"table":table,"value":newvalue},
				    				method : 'post',
				    				callback : function  (options, success, response){
				    					var lis=document.getElementById('progress').getElementsByTagName('li');
				    					var res = new Ext.decode(response.responseText);
				    					if(res.res==true){
				    						for(var x=0;x<initabled.length;x++){
				    							if(initabled[x].VALUE==newvalue){
				    								initabled[x].INITABLED=1;
				    							}
				    						}
				    						for(var i=0;i<lis.length;i++){
				    						if(lis[i].getAttribute("value")==newvalue){
					    						 lis[i].getElementsByTagName('span')[0].setAttribute("class","bluebackground");
				    						}
				    						/*btn.hide();*/
				    					}
				    				}else{
				    					/*showResult("提示",newhtml+"数据为空,初始化失败!");*/
				    				 }
				    			}
							});
								showResult('提示','企业信息确认成功!');
							}else if(res.exceptionInfo != null){
								form.setLoading(false);
								showResult('提示',res.exceptionInfo+'');return false;
		            		}
						}
					});
				}
			}
		},
		{
            id:'fileform',
            xtype:'form',
            width: 'auto',
            bodyStyle: 'background: transparent no-repeat 0 0;border: none;left:100px',
            x:'60%',
            y:10,
            layout:'absolute',
            items:[{
                name:'de_logo',
                id:'de_logo',
                xtype: 'filefield',
                labelStyle:"color:black;",
                columnWidth:1,
                name: 'file',
                buttonOnly:true,
                createFileInput : function() {
	                   var a = this;
	                   a.fileInputEl = a.button.el.createChild({
				           name : a.getName(),
				           cls : Ext.baseCSSPrefix + "form-file-input",
				           tag : "input",
				           type : "file",
				           size : 1,
				           accept:"image/*"
			          }).on("change", a.onFileChange, a)
                },
                buttonConfig:{
                    text:'公司LOGO:上传文件',
                    iconCls:'x-button-icon-pic',
                    width:140,
                    height:25,
                    margin:'5 0 15 0',
                    x:20,
                    y:0
                },
                listeners: {
                    change: function(field){
                    	field.ownerCt.upload(field);
                    }
                }
            },
            {
                xtype: 'image',
                width: 300,
                height: 200,
                id:'logo1',
                /*hidden:true,*/
                margin:'20 0 20 0',
                x:20,
                y:35,
                border: 1,
                style: {
                    borderColor: '#9D9D9D',
                    borderStyle: 'solid',
                    color:'red',
                  
                },
                listeners: {
                	afterrender: function(field){
                    	Ext.Ajax.request({
        					url: basePath + 'ma/logo/hasLogo.action',
        					success:function(fp, o,rep){
        						if(fp.responseText=='true'){
        							field.setSrc(basePath+'ma/logo/get.action');
        						}
        					}						
        				});
                    }
                }
            },{
                xtype: 'label',			                 
               html: '<span class="image-tishi">(说明：请上传大小小于20K的png格式图片)</span>',
                /*value:'(说明：请上传大小小于20K的图片)',*/
		        margin:'35 0 10 0',
		        width:260,
		        height:50,
		        x:20,
		        y:250
            },],
             upload: function(field){
                        field.ownerCt.getForm().submit({
                        	url:basePath+'ma/logo/saveSaasLogo.action' /*basePath + 'b2c/product/saveBrandLogo.action'*/,
                            waitMsg:'正在上传',
                            success: function(fp, o){
                                if(o.result.success){
                                    Ext.Msg.alert('提示','上传成功');  
                                    var logo=Ext.getCmp('logo1');
                                    console.log(Math.random());
                                   logo.setSrc(basePath+'ma/logo/get.action'+'?'+Math.random());
                                }else Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
                            },
                            failure:function(fp,o){
                                Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
                            }
                        });                             
            }
          }
		];
		form.add(items);
	}
});