Ext.define('erp.view.hr.emplmana.StartExamForm1',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.StartExamForm1',
	id: 'form', 
/*	title: '试 卷 ',*/
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
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=").split('=')[1];
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
			url : basePath + 'hr/emplmana/checkExam.action',
			params: params,
			method : 'post',
			callback : function(options, success, response){
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				var items=new Array(),
				data=res.data,
				qtype ='',
				detno=0,
				numb=['一、','二、','三、','四、','五、','六、','七、'];
				form.hasjudged=res.hasjudged;
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
						item.fieldLabel=data[index][1]+'.'+data[index][2]+'<span style="color:#666666">('+data[index][6]+'分)</span>';
						item.labelAlign='top';
						item.labelSeparator='';
						item.labelStyle='background:#E4F2FD;';
						item.exdid=data[index][0];
						//item.labelStyle='background:#FFFAFA;color:#515151;';
   	    	     		item.columns=1;
   	    	     		item.items=new Array();
   	    	     		var answs=data[index][4].split("#");
   	    	    		Ext.each(answs,function(name,inde){
   	    	    			var i=new Object();
   	    	    			i.xtype= 'radiofield';
   	    			        i.name= data[index][1]+'.'+data[index][2];
   	    			        i.disabled=true;
   	    			        i.disabledCls="";
   	    			        i.id=i.name+''+inde;
   	    			        i.anvalue= ["A","B","C","D","E","F","G"][inde];
   	    			        i.labelStyle='background:#FFFFFF;';
   	    			        if(i.anvalue==data[index][8]||data[index][8].indexOf(i.anvalue)>-1)
   	    			        	i.checked=true;
   	    			        if(inde==(answs.length-1)){
   	    			        	i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "")+'<div><span style="color:#0D04FF">正确答案:'+data[index][5]+'</div>';
   	    			        }else{
   	    			        	i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "");
   	    			        }
   	    			        item.items.push(i);
   	    	    		});
   	    	    		item.stanscore=data[index][6];//实际得分
   	    	    		item.score=data[index][7]==null?0:data[index][7];//实际得分
   	    	    		items.push(item);
					}else if(data[index][3]=='多选题'){
						item.xtype='checkboxgroup';
						item.id='q_'+data[index][1];
						item.fieldLabel=data[index][1]+'.'+data[index][2]+'<span style="color:#666666">('+data[index][6]+'分)</span>';
						item.labelAlign='top';
						item.labelSeparator='';
						item.exdid=data[index][0];
						item.labelStyle='background:#E4F2FD;';
						item.columns=1;
   	    	     		item.items=new Array();
   	    	     		var answs=data[index][4].split("#");
   	    	    		Ext.each(answs,function(name,inde){
   	    	    			var i=new Object();
   	    	    			i.disabled=true;
   	    			        i.disabledCls="";
   	    	    			i.xtype= 'checkboxfield';
   	    			        i.name= data[index][1]+'.'+data[index][2];
   	    			        i.anvalue= ["A","B","C","D","E","F","G"][inde];
   	    			        i.boxLabel= answs[inde];
   	    			        if(data[index][8]&&data[index][8].indexOf(i.anvalue)>-1){
   	    			        	i.checked=true;
   	    			        }
	   	    			    if(inde==(answs.length-1)){
		    			        i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "")+'<div><span style="color:#0D04FF">正确答案:'+data[index][5]+'</div>';
		    			       }else{
		    			        i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "");
		    			       }
   	    			        item.items.push(i);
   	    	    		});
   	    	    		item.stanscore=data[index][6];//实际得分
   	    	    		item.score=data[index][7]==null?0:data[index][7];//实际得分
   	    	    		items.push(item);
					}else if(data[index][3]=='判断题'){
						item.xtype='radiogroup';
						item.id='q_'+data[index][1];
						item.fieldLabel=data[index][1]+'.'+data[index][2]+'<span style="color:#666666">('+data[index][6]+'分)</span>';
						item.labelAlign='top';
						item.labelSeparator='';
						item.labelStyle='background:#E4F2FD;';
						item.exdid=data[index][0];
						//item.labelStyle='background:#FFFAFA;color:#515151;';
   	    	     		item.columns=1;
   	    	     		item.items=new Array();
   	    	     		var answs=data[index][4].split("#");
   	    	    		Ext.each(answs,function(name,inde){
   	    	    			var i=new Object();
   	    	    			i.xtype= 'radiofield';
   	    			        i.name= data[index][1]+'.'+data[index][2];
   	    			        i.disabled=true;
   	    			        i.disabledCls="";
   	    			        i.id=i.name+''+inde;
   	    			        i.anvalue= ["对","错"][inde];
   	    			        i.labelStyle='background:#FFFFFF;';
   	    			        if(i.anvalue==data[index][8]||data[index][8].indexOf(i.anvalue)>-1)
   	    			        	i.checked=true;
   	    			        if(inde==(answs.length-1)){
   	    			        	i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "")+'<div><span style="color:#0D04FF">正确答案:'+data[index][5]+'</div>';
   	    			        }else{
   	    			        	i.boxLabel= answs[inde].replace(/(^\s*)|(\s*$)/g, "");
   	    			        }
   	    			        item.items.push(i);
   	    	    		});
   	    	    		item.stanscore=data[index][6];//实际得分
   	    	    		item.score=data[index][7]==null?0:data[index][7];//实际得分
   	    	    		items.push(item);
					}else if(data[index][3]=='填空题'){
						item.xtype='checkboxgroup';
						//item.xtype='textareafield';
						item.exdid=data[index][0];
						item.id='q_'+data[index][1];
						item.labelAlign='top';
						item.whoami='jianda';
						item.stanscore=data[index][6];//满分
						var valu=data[index][7]==null?'':data[index][7];//实际得分
						var answ=data[index][8]==null?'无':data[index][8];//考生答案
						answ = answ=='null'?'无':answ;
						var ansright = data[index][5] == null?'':data[index][5];//标准答案
						if(res.hasjudged=='是'){
							item.fieldLabel='<div class = "title">'+data[index][1]+'.'+data[index][2]
							+'<span style="color:#666666">('+item.stanscore+'分)</span><span style="color:red">&nbsp;&nbsp;得分：'+valu+'</span></div>'+'<div>'+answ+'</div>'
							+'<div><span style="color:#0D04FF">参考答案:'+ansright+'</div>';
							if ( Number(valu) > 0 && Number(valu) == Number(item.stanscore)){//全部得分
								item.isRight = 'right';
							}else if ( Number(valu) > 0 && Number(valu) < Number(item.stanscore)){//部分得分
								item.isRight = 'rightPart';
							}else{//错误
								item.isRight = 'error';
							}
						}else{
							item.fieldLabel='<div class ="title">'+data[index][1]+'.'+data[index][2]
							+'<span style="color:#666666">('+item.stanscore+'分)</span><span>&nbsp;&nbsp;得分：<input type="text" value="'+valu+'" id=s_'+data[index][1]+' style="width=60px;"></span> </div>'+'<div>'+answ+'</div>'
							+'<div><span style="color:#0D04FF">参考答案：'+ansright+'</div>';
						}
						item.labelSeparator='';
						/*item.width=800;*/
						item.value=data[index][8];
						items.push(item);
					}else if(data[index][3]=='简答题'){
						item.xtype='checkboxgroup';
						//item.xtype='textareafield';
						item.exdid=data[index][0];
						item.id='q_'+data[index][1];
						item.labelAlign='top';
						item.whoami='jianda';
						item.stanscore=data[index][6];//满分
						var valu=data[index][7]==null?'':data[index][7];//实际得分
						var answ=data[index][8]==null?'无':data[index][8];//考生答案
						answ = answ=='null'?'无':answ;
						var ansright = data[index][5] == null?'':data[index][5];//标准答案
						if(res.hasjudged=='是'){
							item.fieldLabel='<div class = "title">'+data[index][1]+'.'+data[index][2]
							+'<span style="color:#666666">('+item.stanscore+'分)</span><span style="color:red">&nbsp;&nbsp;得分：'+valu+'</span></div>'+'<div>'+answ+'</div>'
							+'<div><span style="color:#0D04FF">参考答案:'+ansright+'</div>';
							if ( Number(valu) > 0 && Number(valu) == Number(item.stanscore)){//全部得分
								item.isRight = 'right';
							}else if ( Number(valu) > 0 && Number(valu) < Number(item.stanscore)){//部分得分
								item.isRight = 'rightPart';
							}else{//错误
								item.isRight = 'error';
							}
						}else{
							item.fieldLabel='<div class ="title">'+data[index][1]+'.'+data[index][2]
							+'<span style="color:#666666">('+item.stanscore+'分)</span><span>&nbsp;&nbsp;得分：<input type="text" value="'+valu+'" id=s_'+data[index][1]+' style="width=60px;"></span> </div>'+'<div>'+answ+'</div>'
							+'<div><span style="color:#0D04FF">参考答案：'+ansright+'</div>';
						}
						item.labelSeparator='';
						/*item.width=800;*/
						item.value=data[index][8];
						items.push(item);
					}
				});
				form.add(items);
				if(form.hasjudged=='是'){
					Ext.getCmp('SubmitExam').hide()
				}
			}
		});
	},changeCss:function(form){
		// items
		Ext.each(form.items,function(item,inde){
			var qid = item.id;
		    var o = document.getElementById('a_'+qid.split('_')[1]);
		    if (o != null){
			    switch(item.isRight){
				case 'right':
				 	o.style.background="#E4F2FD";
				  	break;
				case 'rightPart':
				 	o.style.background="#A7CEEE";
					break;
				case 'error':
					o.style.background="red";
					o.style.color="#ffffff";
					break;
				default :
				  o.style.background="red";
			      o.style.color="#ffffff";
				}
		    }
		});
		
	}
});