Ext.QuickTips.init();
Ext.define('erp.controller.plm.test.CheckBase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'plm.test.Check','core.form.Panel','core.grid.Panel2','core.grid.Panel5',
    		'core.button.Submit','core.button.ResSubmit','core.button.Close',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.ChangeHandler'
    	],
    init:function(){
      var me=this;
    	this.control({ 
    		'erpGridPanel2':{   //获得数据显示在GRID上，并且根据store 中ID内容隐藏Grid
    			storeloaded:function(){
       				var grid = Ext.getCmp("grid");
    				var store = grid.store;
    				var groups = grid.store.getGroups();
    				var child = groups[0].children;
    				var data = child[0].data;
    				var ch_id = data.ch_id;
    				if(ch_id==''||ch_id==null||ch_id==0){
    					grid.hide();
    				}
    			},    		
    			afterrender:function(p){
    				var id = this.getUrlParam("formCondition").split("=")[1];
    				Ext.Ajax.request({
    					url : basePath + "plm/test/getChecklistGridData.action",
    					params: {
    						id : id
    					},
    					method : 'post',
    					async: false,
    					callback : function(options, success, response){
    						var res = new Ext.decode(response.responseText);
    						if (res.success) {
    		                    var grid = Ext.getCmp('grid');
    		                    grid.GridUtil.loadNewStore(grid, {
    		                        caller: "CheckListBaseDetail",
    		                        condition: 'ch_cbdid=' + id
    		                    });

    		                }
    					}
    				});
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){   		
                  var code = Ext.getCmp('cbd_statuscode').getValue();
//                  if(code!='TESTING'){
//                	btn.hide();
//                  }
    			},
    			beforerender:function(btn){
    				btn.formBind=true;
    			},
    			click: function(btn){				
    			  var handman=Ext.getCmp('cbd_handman').value,result=Ext.getCmp('cbd_result').value;
                  if(result=='NG' && (handman == null || handman=='')){
                	 showError('NG状态下请先选择问题处理人再提交'); 
                  }
                  else me.onUpdate(this);
    			}
    		},  
    		'erpResSubmitButton':{
    			click:function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('cbd_id').getValue());
    			},
    			afterrender: function(btn){   		
                    var code = Ext.getCmp('cbd_statuscode').getValue();
//                    if(code=='TESTING'){
                  	btn.hide();
//                    }
      			},
    			
    		},
    		'erpCloseButton':{
    			afterrender:function(btn){
    			/*var handmanid=Ext.getCmp('cld_newhandmanid').getValue();
    			if(Ext.getCmp('cld_statuscode').value!='HANDED'){
    			if(handmanid==emid){
    				Ext.getCmp('cld_newhanddate').setValue(Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    				if(Ext.getCmp('cld_statuscode').value!='HANDED'){
    					Ext.getCmp('cld_handdescription').setValue(null);
    					Ext.getCmp('cld_handresult').setValue(null);
    				}
    			}else {
    					Ext.getCmp('cld_newtestdate').setValue(Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    					Ext.getCmp('cld_newtestman').setValue(emname);
    					Ext.getCmp('cld_newtestmanid').setValue(emid);
    					Ext.getCmp('cld_testdescription').setValue(null);
    					Ext.getCmp('cld_testresult').setValue(null);
    			}
    			}else {
    				  
    				
    				
    			}
    			var value=Ext.getCmp('cld_attach').getValue();
    			 var form = me.getForm(btn);
    			if(value!=null&&value!=""){
    			 form.add({
    		         title:'相关文件',
    		         id:'container',
    		         style: {borderColor:'green', borderStyle:'solid', borderWidth:'0px'},
    		         xtype:'container',
    		         columnWidth:1
    		        });
    			 var name=value.split(";")[0];
    			 var id=value.split(";")[1];
    	        		var items = new Array();
    	        		items.push({
    				    style: 'background:#CDBA96;',
    					html: '<h1>相关文件:</h1>',
    					});
    	        		 items.push({
     					    
     						style: 'background:#C6E2FF;',
     						html: '<img src="' + basePath + 'resource/images/mainpage/things.png" width=16 height=16/>' + 
     						 '<span>文件:' + name + '<a href="' + basePath + "common/downloadbyId.action?id=" + id + '">下载</a></span>',
     					});   	 
    	        		 Ext.getCmp('container').add(items);
    			}*/
    			
    			}
    		},
    		'textfield[name=cbd_testman]':{
    			afterrender:function(field){
    				if(!field.value){
    					field.setValue(emname);
    				}
    			}
    		},
    		'textfield[name=cbd_result]':{
    			afterrender:function(field){
    				if(field.value){
    				field.setValue('');
    				}
    			},
    			change:function(field){
    				 var handman=Ext.getCmp('cbd_handman').value,result=Ext.getCmp('cbd_result').value,testdescription=Ext.getCmp('cbd_testdescription').value;
    				 var btn = Ext.getCmp('submit');
    				 if(result=='NG' && (handman == null || handman=='')){
    					 btn && btn.setDisabled(true);
    				 }else if(result!='NG' && (testdescription != null || testdescription!='')){
    					 btn && btn.setDisabled(false);
    				 }
    			}
    		},
    		'textfield[name=cbd_problemgrade]':{
    			afterrender:function(field){
    				if(field.value){
    				field.setValue('');
    				}
    			}
    		},
    		'textfield[name=cbd_problemrate]':{
    			afterrender:function(field){
    				if(field.value){
    				field.setValue('');
    				}
    			}
    		},
    		/*'textfield[name=cbd_testdescription]':{
    			afterrender:function(field){
    				if(field.value){
    				field.setValue('');
    				}
    			}
    		},*/
    		'datetimefield[name=cbd_testdate]':{
    			afterrender:function(field){
    				if(!field.value){
    				field.setValue(new Date());
    				}
    			}
    		},
    		'multidbfind[name=cbd_handman]':{
    			afterrender:function(field){
    				field.dbKey='cb_prjid';
    				field.mappingKey='tm_prjid';
    				field.message='无数据!';
    			}
    		},
    		'combo[name=cbd_result]':{
    			afterrender: function(t) {
    				me.checkType(t.value);
    			},
    			change: function(t){
    				me.checkType(t.value);
    			}
    		 },
    		'erpYnField[name=cld_handresult]':{
    			beforerender:function(field){
    			var value=Ext.getCmp('cld_newhandmanid').getValue();
    		     if(value&&value==emid){
    		    	 //当前用户进来的是处理人员
    		    	field.fieldStyle='background:#fffac0;color:#515151;';
    		    	field.allowBlank=false;
    		    	field.readOnly=false;
    		     }
    		  }   			
    		},
    		'erpYnField[name=cld_testresult]':{
    			beforerender:function(field){
    			var value=Ext.getCmp('cld_newhandmanid').getValue();
    		     if(value&&value!=emid){
    		    	 //当前用户进来的是处理人员		   
    		    	field.allowBlank=false;
    		    	field.fieldStyle='background:#fffac0;color:#515151;';
    		    	field.readOnly=false;
    		     }
    		  }   			
    		},
    		'textarea[name=cld_testdescription]':{
    			beforerender:function(field){
        			var value=Ext.getCmp('cld_newhandmanid').getValue();
        		     if(value&&value!=emid){
        		    	 //当前用户进来的是处理人员
        		    	field.allowBlank=false;
         		    	field.fieldStyle='background:#fffac0;color:#515151;';
         		    	field.readOnly=false;         		    
        		     }
        		  }   		
    		},
    		'textarea[name=cld_handdescription]':{
    			beforerender:function(field){
        			var value=Ext.getCmp('cld_newhandmanid').getValue();
        		     if(value&&value==emid){
        		    	 //当前用户进来的是处理人员
        		    	field.fieldStyle='background:#fffac0;color:#515151;';
        		    	field.allowBlank=false;
        		    	field.readOnly=false;
        		    	field.value="";
        		     }
        		  }   		
    		},
    		'gridcolumn[dataIndex=ch_description]':{
    			beforerender:function(column){
    				column.flex=1;
    			}
    		},
    		'dbfindtrigger[name=cld_newhandman]':{
      		  afterrender:function(trigger){    	        
        			trigger.dbKey='cl_prjplanid';
        			trigger.mappingKey='tm_prjid';
        			trigger.dbMessage='请选择该测试单的项目计划';
      	     },    		
      		},
    		'dbfindtrigger[name=prjplan_prjid]':{
    		  afterrender:function(dbfindtrigger){    	        
               dbfindtrigger.dbBaseCondition="prj_statuscode IS 'AUDITED'";    	        
    	     },    		
    		},
    		'textfield[name=cld_name]':{
    		  render:function(field){
    			   Ext.create('Ext.tip.ToolTip', {
				        target:field.getEl(),    					        
				        trackMouse: true,
				        renderTo: Ext.getBody(),
				        html:field.value,
				        bodyStyle: {
				            background: '#F8F8FF',
				            padding: '10px'
				        }
				    });
    		   }
    		},
    		'textfield[name=cl_prjplanname]':{
    			 render:function(field){
      			   Ext.create('Ext.tip.ToolTip', {
  				        target:field.getEl(),    					        
  				        trackMouse: true,
  				        renderTo: Ext.getBody(),
  				        html:field.value,
  				        bodyStyle: {
  				            background: '#F8F8FF',
  				            padding: '10px'
  				        }
  				    });
      		   }
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onUpdate: function(me){
		var mm = this;
		var form = Ext.getCmp('form');
		var s1 = mm.checkFormDirty(form);
		var s2 = '';
		var grids = Ext.ComponentQuery.query('gridpanel');
		if(grids.length > 0){//check所有grid是否已修改
			Ext.each(grids, function(grid, index){
				if(grid.GridUtil){
					var msg = grid.GridUtil.checkGridDirty(grid);
					if(msg.length > 0){
						s2 = s2 + '<br/>' + grid.GridUtil.checkGridDirty(grid);
					}
				}
			});
		}
		if(s1 == '' && (s2 == '' || s2 == '<br/>')){
			showError($I18N.common.form.emptyData + '<br/>' + $I18N.common.grid.emptyDetail);
			return;
		}
		if(form && form.getForm().isValid()){
			//form里面数据
			var r = form.getValues(false, true);
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!mm.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			var params = [];
			if(grids.length > 0){
				var param = grids[0].GridUtil.getGridStore();
				if(grids[0].necessaryField.length > 0 && (param == null || param == '')){
					warnMsg('明细表还未添加数据,是否继续?', function(btn){
						if(btn == 'yes'){
							params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
						} else {
							return;
						}
					});
				} else {
					params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
				}
			}
			mm.update(r, params);
		}else{
			mm.checkForm(form);
		}
	},
	update: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
		}
		var mm = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params: params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage('提示', '提交成功!', 1000);
    				window.location.reload();
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					showError(str);
	   					window.location.reload();
	   				}
        			showError(str);return;
        		} else {
	   				updateFailure();
	   			}
	   		}
		});
	},
	checkFormDirty: function(){
		var form = Ext.getCmp('form');
		var s = '';
		form.getForm().getFields().each(function (item,index,length){
			var value = item.value == null ? "" : item.value;
			item.originalValue = item.originalValue == null ? "" : item.originalValue;
			if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
				var label = item.fieldLabel || item.ownerCt.fieldLabel ||
					item.boxLabel || item.ownerCt.title;//针对fieldContainer、radio、fieldset等
				if(label){
					s = s + '&nbsp;' + label.replace(/&nbsp;/g,'');
				}
			}
		});
		return (s == '') ? s : ('表单字段(<font color=green>'+s+'</font>)已修改');
	},
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
	},
	checkType: function(val){
		if(val != 'NG') {
			Ext.getCmp('cbd_handman').allowBlank = true;
			Ext.getCmp('cbd_handman').setFieldStyle('background:#FFFAFA;color:#515151;');
		} else {
			Ext.getCmp('cbd_handman').allowBlank = false;
			Ext.getCmp('cbd_handman').setFieldStyle("background:#E0E0FF;color:#515151;");

		}
	},
	getUrlParam: function(name){
		var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
	    var r=window.location.search.substr(1).match(reg);   
	    if(r!=null)   
	    	return decodeURI(r[2]); 
	    return null; 
	}
});