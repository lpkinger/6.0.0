Ext.define('erp.view.hr.emplmana.StartExamForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.StartExamForm',
	id: 'form', 
	/*title: '试 卷 ',*/
   // frame : true,
	autoScroll : true,
	bodyStyle:'background:transition !important',
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       //fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		formCondition = getUrlParam('id');//从url解析参数
		formCondition = (formCondition == null) ? id : formCondition.replace(/IS/g,"=");
		//集团版
		var master=getUrlParam('newMaster');
		var param = {caller: this.caller || caller, id: this.formCondition || formCondition, _noc: (getUrlParam('_noc') || this._noc)};
		if(master){
			param.master=master;
		}
		this.createItemsAndButtons(this,this.params || param);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	createItemsAndButtons:function(form,params){
		Ext.Ajax.request({//拿到form的items
			url : basePath + 'hr/emplmana/getExam.action',
			params: params,
			method : 'post',
			callback : function(options, success, response){
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				var items=new Array();
				var data=res.data;
				var qtype ='',
				detno=0,
				numb=['一、','二、','三、','四、','五、','六、','七、'];
				Ext.each(data,function(name,index){
					var item=new Object(),
						itemTitle=new Object();
					if(qtype != data[index][3]){
				        itemTitle.xtype='label';
						itemTitle.id='qtitle_'+data[index][1];
						itemTitle.labelAlign='top';
						itemTitle.text=numb[detno++]+data[index][3];
						itemTitle.cls='qtitle';
						if(qtype==''){
							itemTitle.cls='qtitle qtitleFirst';
						}
						items.push(itemTitle);
						qtype = data[index][3];
					}
					item.cls='questions';
					if(data[index][3]=='单选题'){
						item.xtype='radiogroup';
						item.id='q_'+data[index][1];
						item.fieldLabel=data[index][1]+'.'+data[index][2];
						item.labelAlign='top';
						item.labelSeparator='';
						item.labelStyle='background:#E4F2FD;';
						item.exdid=data[index][0];
						item.allowBlank = false;
						//item.labelStyle='background:#FFFAFA;color:#515151;';
   	    	     		item.columns=1;
   	    	     		item.items=new Array();
   	    	     		var answs=data[index][4].split("#");
   	    	    		Ext.each(answs,function(name,inde){
   	    	    			var i=new Object();
   	    	    			i.xtype= 'radiofield';
   	    			        i.name= data[index][1]+'.'+data[index][2];
   	    			        i.id=i.name+''+inde;
   	    			        i.anvalue= ["A","B","C","D","E","F","G"][inde];
   	    			        i.labelStyle='background:#FFFFFF;';
   	    			        i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "");
   	    			        item.items.push(i);
   	    	    		});
   	    	    		items.push(item);
					}else if(data[index][3]=='多选题'){
						item.xtype='checkboxgroup';
						item.id='q_'+data[index][1];
						item.fieldLabel=data[index][1]+'.'+data[index][2];
						item.labelAlign='top';
						item.labelSeparator='';
						item.allowBlank = false;
						item.exdid=data[index][0];
						item.labelStyle='background:#E4F2FD;';
						item.columns=1;
   	    	     		item.items=new Array();
   	    	     		var answs=data[index][4].split("#");
   	    	    		Ext.each(answs,function(name,inde){
   	    	    			var i=new Object();
   	    	    			i.xtype= 'checkboxfield';
   	    			        i.name= data[index][1]+'.'+data[index][2];
   	    			        i.anvalue= ["A","B","C","D","E","F","G"][inde];
   	    			        i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "");
   	    			        item.items.push(i);
   	    	    		});
   	    	    		items.push(item);
					}else if(data[index][3]=='判断题'){
						item.xtype='radiogroup';
						item.id='q_'+data[index][1];
						item.fieldLabel=data[index][1]+'.'+data[index][2];
						item.labelAlign='top';
						item.labelSeparator='';
						item.labelStyle='background:#E4F2FD;';
						item.exdid=data[index][0];
						item.allowBlank = false;
						//item.labelStyle='background:#FFFAFA;color:#515151;';
   	    	     		item.columns=1;
   	    	     		item.items=new Array();
   	    	     		var answs=data[index][4].split("#");
   	    	    		Ext.each(answs,function(name,inde){
   	    	    			var i=new Object();
   	    	    			i.xtype= 'radiofield';
   	    			        i.name= data[index][1]+'.'+data[index][2];
   	    			        i.id=i.name+''+inde;
   	    			        i.anvalue= ["对","错"][inde];
   	    			        i.labelStyle='background:#FFFFFF;';
   	    			        i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "");
   	    			        item.items.push(i);
   	    	    		});
   	    	    		items.push(item);
					}else if(data[index][3]=='填空题'){
						item.xtype='textareafield';
						item.exdid=data[index][0];
						item.id='q_'+data[index][1];
						item.labelAlign='top';
						item.allowBlank = false;
						item.fieldLabel=data[index][1]+'.'+data[index][2];
						item.labelSeparator='';
						item.maxLength = 4000;
						item.width=800;
						items.push(item);
					}else if(data[index][3]=='简答题'){
						item.xtype='textareafield';
						item.exdid=data[index][0];
						item.id='q_'+data[index][1];
						item.labelAlign='top';
						item.allowBlank = false;
						item.fieldLabel=data[index][1]+'.'+data[index][2];
						item.labelSeparator='';
						item.maxLength = 4000;
						item.width=800;
						items.push(item);
					}
				});
				form.add(items);
			}
		});
	}
//	buttons: [/*{
//		xtype: 'erpStartAccountButton'
//	},*/{
//		xtype: 'erpSubmitButton'
//	},{
//		xtype: 'erpDeleteButton'
//	}]
});