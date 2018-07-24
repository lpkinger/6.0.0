Ext.QuickTips.init();
/*Ext.override(Ext.form.action.Submit, {
	  doSubmit: function() {
        var formEl,
            ajaxOptions = Ext.apply(this.createCallback(), {
                url: this.getUrl(),
                method: this.getMethod(),
                headers: this.headers
            });
        if (this.form.hasUpload()) {
            formEl = ajaxOptions.form = this.buildForm();
            ajaxOptions.isUpload = true;
        } else {
            ajaxOptions.params = this.getParams();
        }
        console.log(ajaxOptions);
        Ext.Ajax.request(ajaxOptions);

        if (formEl) {
            Ext.removeNode(formEl);
        }
    }
});*/
Ext.define('erp.controller.common.MultiFileUpload', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:['common.multiFileUpload.Form','common.multiFileUpload.GridPanel'],
    init:function(){
    	var me = this;
    	//存放临时批次号
    	var id;
    	this.getFiles = function(){
    		var fileDom = Ext.getCmp('fileform').getEl().down('input[type=file]');
			return fileDom.dom.files            
    	},
    	this.control({
    		'erpMultiFileUploadFormPanel filefield[name=files]':{
    			change:function(t){
                    var files = me.getFiles(),
                    fileArr = [];
                    for(var i = 0; i<files.length; i++){
                    	fileArr.push({
                    		fl_id:'',
                    		fl_name:files[i].name,
                    		fl_matchres:'',
                    		fl_matchstatus:'',
                    		fl_matchcode:'',
                    		fl_uploadres:'',
                    		fl_uploadstatus:'',
                    		fl_deptno:i+1
                    	})
                    }
                    id = me.putGridData(fileArr);
                    //是上传按钮可用
                    Ext.getCmp('uploadConfirm').enable();
    			}
    		},
    		//上传
    		'erpMultiFileUploadFormPanel button[name=uploadConfirm]':{
    			click:function(){
    				store = Ext.getCmp('fileUploadgrid').getStore(),
    				datatype = Ext.getCmp('datatype').getValue(),
    				files = me.getFiles();;
    				//上传之前的检查
    				me.beforeUpload(me,store,datatype,files,id);
    			}
    		},
    		'erpMultiFileUploadFormPanel button[name=doMatchData]':{
    			click:function(){
    				var datatype = Ext.getCmp('datatype').getValue();
    				if (datatype && id) {
    					Ext.Ajax.request({
							url:basePath + 'common/doMatchData.action',
							params:{
								datatype:datatype,
								id:id
							},
							method : 'get',
							async:false,
							callback:function(records, options, response){
								var rs = Ext.decode(response.responseText);
								if (rs.success) {
									me.getGridDataById(id,'match')
								}
							}
					 	});
    				}
    			}
    		},
    		//导出
    		'erpMultiFileUploadFormPanel button[name=exportdata]':{
    			click:function(){
    			}
    		},
    		//只显示未匹配
    		'erpMultiFileUploadFormPanel checkbox[name=showNoMatch]':{
    			change:function(t,n,o){
    				var store = Ext.getCmp('fileUploadgrid').getStore();
    				if (n==true) {
    					store.filter('fl_matchstatus',0);
    				}else{
    					store.clearFilter();
    				}
    			}
    		}
    	});
    },
    beforeUpload:function(me,store,datatype,files,id){
    	//检查是否有权限
    	if(!me.checkUploadPower()){
			return;
		}
		//有没有按钮匹配按钮
		var noMatchButton = false;
		Ext.Array.each(store.data.items,function(rec){
			if (rec.data.fl_matchstatus==null) {
				noMatchButton = true;
				return false;
			}
		});
		//是否有未匹配的记录
		var hasNoMatch = store.findRecord('fl_matchstatus',0);
		if (noMatchButton) {
			Ext.Msg.alert('提示', '请先匹配附件信息');
		}else if(hasNoMatch){
			//存在不匹配
			Ext.MessageBox.confirm('提示',
			'您有未匹配到基础信息的附件,继续执行将只上传匹配到的附件,请确认是否继续',function(t){
				if (t=='yes') {
					//执行上传
					 Ext.MessageBox.show({
					 	   title:'提示',
				           msg: '正在上传附件,请稍等',
				           width:300,
				           wait:true,
				           waitConfig: {interval:200}
				     });
				       
					
					store.each(function(record){
						if (record.get('fl_matchstatus')==1) {
							//将这个匹配成功的file上传
							me.multiFileUpload1(files[record.get('fl_deptno')-1],id,datatype);
						}
					});
					
					Ext.defer(function(){
				    	Ext.MessageBox.hide();
						//刷新grid,显示上传结果
						me.getGridDataById(id,'upload');
						//使按钮不可用
						Ext.getCmp('uploadConfirm').disable();
				    },1500)  
				
				}else if(t=='no'){
					return;
				}
			});
		}else{
			//没有不匹配的
			Ext.MessageBox.show({
			 	   title:'提示',
		           msg: '正在上传附件,请稍等',
		           width:300,
		           wait:true,
		           waitConfig: {interval:200}
		     });
			
			store.each(function(record){
				me.multiFileUpload1(files[record.get('fl_deptno')-1],id,datatype);
			})
			
			Ext.defer(function(){
		    	Ext.MessageBox.hide();
				//刷新grid,显示上传结果
				me.getGridDataById(id,'upload');
				//使按钮不可用
				Ext.getCmp('uploadConfirm').disable();
		    },1500)
		}
    },
    checkUploadPower:function(){
    	var success = true;
    	Ext.Ajax.request({
			url:basePath + 'common/checkUploadPower.action',
			params:{
				caller:caller
			},
			method : 'get',
			async:false,
			callback:function(records, options, response){
				var rs = Ext.decode(response.responseText);
				if (!rs.success) {
					Ext.MessageBox.alert("警告","对不起，您不是管理员或在该页面没有权限!");
					success = false;
				}
			}
	 	});
	 	return success;
    },
    multiFileUpload1:function(file,id,datatype){
		  var me = this;
		 　try {
		 	 var fd = new FormData();
			 var ajax = new XMLHttpRequest();
			 fd.append("id", id);
			 fd.append("em_code", em_code);
			 fd.append("datatype",datatype);
			 fd.append("file", file);
			 ajax.open("post", basePath + "common/uploadMulti.action", false);
			 ajax.onload = function () {};
			 ajax.send(fd);
		　} catch(error) {
			//意外时，去更新表中当前上传失败文件的状态
		 	Ext.Ajax.request({
				url:basePath + 'common/extraUploadFail.action',
				params:{
					filename:file.name,
					id:id
				},
				method : 'get',
				async:false,
				callback:function(records, options, success){
				}
		 	});
		　} 
    },
    
    showUploadres:function(store){
		var uploadres = Ext.getCmp('uploadres'),
		count = store.getCount(),
		succ = 0;
		store.each(function(record){
			if (record.get('fl_uploadstatus')==1) {
				succ++;
			}
		});
		uploadres.setValue("成功"+succ+"条,失败"+(count-succ)+"条")
    },
    showMatchres:function(store){
    	var matchres = Ext.getCmp('matchres'),
		count = store.getCount(),
		succ = 0;
		store.each(function(record){
			if (record.get('fl_matchstatus')==1) {
				succ++;
			}
		});
		matchres.setValue("成功"+succ+"条,失败"+(count-succ)+"条")
    },
	getGridDataById:function(id,type){
		var me = this,
    	store = Ext.getCmp('fileUploadgrid').getStore();
		store.getProxy().url = basePath + 'common/getGridData.action'; 
		store.load({
			params:{id:id},
			async:false,
			callback:function(records, options, success){
				if (type=='upload') {
					me.showUploadres(store);
				}else if(type='match'){
					me.showMatchres(store);
				}
			}
		});
	},
    putGridData:function(data){
    	var me = this,
    	grid = Ext.getCmp('fileUploadgrid'),
    	store = grid.getStore(),
    	id = '';
    	Ext.Ajax.request({
    		url:basePath + 'common/putGridData.action',
    		params:{
    			data:Ext.JSON.encode(data)
    		},
    		method : 'post',
			async: false,
			callback : function(options,success,response){
				var rs = Ext.decode(response.responseText);
				if (rs.success) {
					id = rs.id;
					me.getGridDataById(rs.id,'');
				}
			}
    	})
    	return id;
    }
    /*multiFileUpload:function(id){
    	var me = this,grid = Ext.getCmp('fileUploadgrid');
    	var store = grid.getStore();
		var form = Ext.getCmp('fileUploadform')    	
    	var fileform = Ext.getCmp('fileform');
    	fileform.getForm().submit({
    		url:basePath + form.FileUploadUrl,
    		method:'POST',
    		params:{
    			id:id,
    			caller:caller,
    			em_code:em_code
    		},
    		success: function(form, action) {
    			me.getGridDataById(id);
		    }
    	});
    },
    upload:function(form,id){
       var uploadFields=new Array(),pageSize=10;
       form.getForm().getFields().each(function(field) {
            if (field.isFileUpload()) {
               //uploadFields.push(field);
            	var fileInput=field.inputEl.dom;
            	console.log(fileInput);
            	var   clone = fileInput.cloneNode(true);
            	console.log(fileInput.parentNode);
            }
       });
    
            fileInput.parentNode.replaceChild(clone, fileInput);
            me.inputEl = Ext.get(clone);
            
            
       var fileDom=form.getEl().down('input[type=file]');
       var files = fileDom.dom.files;
       alert(files.length);
       
       var len=files.length,arr=null;
       if(len>0){
         var num1=len/pageSize,num2=len%pageSize;
         console.log(num1);
         console.log(num2);
         if(num1>0){
         	console.log(files);
         	for (var i=0;i<num1;i++){
         	   arr=files.slice(i*pageSize,i*pageSize+9); 
         	   console.log('request');
         	   this.pageUpload(this.buildForm(arr),id);
         	}
         }
         if(num2>0){
            arr=files.slice(num1*pageSize,num1*pageSize+num2); 
             console.log('request--');
            this.pageUpload(this.buildForm(arr),id);
         }
         
       	 if(num1>0){
       	   for(ind=1; ind<=num1;ind++){
       	   	  var arr=new Array();
       	     for(var j=(ind-1)*10;j<ind*10;j++){
       	       arr.push(files[j]);
       	     }
       	     this.pageUpload(this.buildForm(arr),id);
       	   }
       	 }
       	 if(num2>0){ 
       	 	   var arr=new Array();
       	   for(var i=(ind-1)*10;i<len;i++){
       	     arr.push(files[i]);     	     
       	   }
       	 	this.pageUpload(this.buildForm(arr),id);
       	 }
       }
       
       
       
    },
    buildForm:function(files){
       var formEl=Ext.DomHelper.append(Ext.getBody(), {
            tag: 'form',
            target: '_self',
            style: 'display:none',
            encoding:'multipart/form-data',
            enctype :'multipart/form-data'
      });
      var me = this,
            fileInput = me.isFileUpload() ? me.inputEl.dom : null,
            clone;
        if (fileInput) {
            clone = fileInput.cloneNode(true);
            fileInput.parentNode.replaceChild(clone, fileInput);
            me.inputEl = Ext.get(clone);
        }
        return fileInput;
        var fileInput=Ext.getCmp('files').inputEl.dom;
        var clonedom=Ext.clone(fileInput);  
        var o={};
       Ext.Array.each(files,function(f,index){
      	  o[index]=f;
       });
       clonedom.files=o;
       var clonenode=fileInput.cloneNode(true);
       
       return formEl;
    },
    pageUpload:function(formEl,id){
       Ext.Ajax.request({
            url:basePath + 'common/uploadMulti.action',
    		method:'POST',
    		form:formEl,
    		isUpload: true,
    		async:false,
    		params:{
    			id:id,
    			caller:caller,
    			em_code:em_code
    		},
    		success: function(form, action) {
    			console.log('success');
		    }
       })
        if (formEl) {
            Ext.removeNode(formEl);
        }
    },*/
    
    
})