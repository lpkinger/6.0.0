Ext.QuickTips.init();
Ext.define('erp.controller.oa.batchMail.batchMail', {
	extend: 'Ext.app.Controller',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.batchMail.batchMail','oa.batchMail.mailSelect','oa.batchMail.mailContent','core.form.Panel','core.trigger.MultiDbfindTrigger',
    		'erp.view.oa.batchMail.orgPanel','erp.view.oa.batchMail.personalGroup','erp.view.core.form.FileField'
    	],
	init: function(){
		var me = this;
		this.control({
			'orgSelectPanel': {
				beforeitemclick: function(tree, record, item, index, e){
					if(e.getTarget('input'))	//如果点击的是checkbox
						return false;
					var parentId=record.data.id;
					if(record.data.id.indexOf('-')>0)
						parentId = parentId.split('-')[0];
					//看是否加载了其children
	            	if (record.childNodes.length == 0 && record.data.leaf == false){
	            		//从后台加载
                		var tree = Ext.getCmp('tree-panel');
               			tree.setLoading(true, tree.body);
               			Ext.Ajax.request({ //拿到tree数据
                		    url: basePath + 'hr/employee/getAllHrOrgsTree.action',
	                        params: {
	                            	parentId: parentId
	                        },
	                   		callback: function(options, success, response) {
	                   			tree.setLoading(false);
	                  		    var res = new Ext.decode(response.responseText);
	                 		    if (res.tree) {
	                 			    Ext.Array.each(res.tree, function(item){
	                 			    	if(item.leaf)
	                    					item.checked = false;
	                 			    });
	                                record.appendChild(res.tree);
	                  		    } else if (res.exceptionInfo) {
	                       		   showError(res.exceptionInfo);
	                  		    }
	                    	}
               			});
	            	}
				},
				checkchange: function(node, checked){
					var email = node.data.data.em_email;
					if(email != null && email != ''){
						if(checked){
							appendSpanByCheckbox(email + ';');
						}else{
							deleteSpanByCheckbox(email + ';');
						}
					}
				},
				itemcontextmenu: function(tree, record, item, index, e){
					// 屏蔽浏览器的右键菜单
					e.preventDefault();
					e.stopEvent();
					//获得已有的个人通讯组
					Ext.Ajax.request({
						url: basePath + 'oa/batchmail/getGroups.action',
						success: function(response){
							var array = new Ext.decode(response.responseText).groups.split(',');
							var items = [],munu;
							Ext.Array.each(array, function(item){
								var obj = {
									text: item,
									handler: function(group){
										var tree = Ext.getCmp('tree-panel');
										var records = tree.getView().getChecked(), array=[];
										if(records.length == 0){
											Ext.Msg.alert('提示', '请至少勾选一条记录!');
										}else{
											Ext.Array.each(records, function(record){
												array.push(Number(record.data.id.split('-')[0]));
											});
											Ext.Ajax.request({
												url: basePath + 'oa/batchmail/addToGroup.action',
												params: {
													groupName: group.text,
													ids: array.join(',')
												},
												success: function(response){
													var res = Ext.decode(response.responseText);
													Ext.Msg.alert('提示', res.msg);
													//清楚勾选的记录
													Ext.Array.each(records, function(item){
														item.set('checked',false);
													});
													//刷新树
													var text = group.text;
													var root = Ext.getCmp('group-tree').store.tree.root,record;
													root.eachChild(function(child){
														if(child.data.text == text)
															record = child;
													});
													Ext.getCmp('group-tree').reloadTree(record);
												}
											});
										}
									}
								};
								if(obj.text != 'null')
									items.push(obj);
							});
							//添加自定义菜单
							menu = Ext.create('Ext.menu.Menu', {
								items: [{
									text: '添加至个人通讯组',
									hideOnClick: false,
									menu: Ext.create('Ext.menu.Menu',{
										items: [{
											text: '新建通讯组',
											handler: function(){
												//判断是否勾选了人
												var tree = Ext.getCmp('tree-panel');
												var records = tree.getView().getChecked();
												if(records.length == 0){
													Ext.Msg.alert('提示', '请至少勾选一条记录!');
												}else{
													//创建新窗口
													if(!win){
														var win = Ext.create('Ext.window.Window', {
															title: '新建通讯组',
															height: 100,
															width: 300,
															closeAction: 'hide',
															items: [{
																xtype: 'form',
																layout: 'anchor',
																bodyPadding: 5,
																items: [{
																	xtype: 'textfield',
																	name: 'folderName',
																	fieldLabel: '名称',
																	allowBlank: false,
																	maxLength: 50,
																}],
																buttons: [{
																	text: '确定',
																	formBind: true,
																	handler: function(btn){
																		//获取勾选的值
																		var form = btn.ownerCt.ownerCt,arr = [];
																		Ext.Array.each(records, function(item){
																			arr.push(Number(item.data.id.split('-')[0]));
																		});
																		var folderName = form.getForm().getFieldValues('folderName');
																		//执行保存
																		Ext.Ajax.request({
																			url: basePath + 'oa/batchmail/createDir.action',
																			params: {
																				folderName: folderName,
																				ids: arr.join(',')
																			},
																			callback: function(options, success, response) {
																				var res = new Ext.decode(response.responseText);
																		        Ext.Msg.alert('提示', res.msg);
																		      //关闭窗口
																				btn.ownerCt.ownerCt.ownerCt.close();
																				//刷新树
																				Ext.getCmp('group-tree').getRootNode();
																				//清楚勾选的记录
																				Ext.Array.each(records, function(item){
																					item.set('checked',false);
																				});
																		    }
																		});
																	}
																},{
																	text: '取消',
																	handler: function(btn){
																		//关闭
																		btn.ownerCt.ownerCt.ownerCt.close();
																	}
																}]
															}],
															listeners: {
																show: function(){
																	Ext.getBody().mask();
																},
																hide: function(){
																	Ext.getBody().unmask();
																}
															}
														});
														win.show();
													}
												}
												
												
											}
										}]
									})
								}]
							});
							menu.items.items[0].menu.add(items);
							menu.showAt(e.getXY());
						}
					});
					
				}
				
			},
			'personalGroupPanel': {
				beforeitemclick: function(tree, record, item, index, e){
					if(e.getTarget('input'))	//如果点击的是checkbox
						return false;
					var groupName = record.data.text;
					//看是否加载了其children
	            	if (record.childNodes.length == 0 && record.data.leaf == false){
	            		//从后台加载
                		var tree = Ext.getCmp('group-tree');
               			tree.setLoading(true, tree.body);
               			Ext.Ajax.request({
               				url: basePath + 'oa/batchmail/getPersonByGroupName.action',
               				params: {
               					groupName: groupName
               				},
               				success: function(response){
               					tree.setLoading(false);
               					var res = Ext.decode(response.responseText);
               					if (res.tree) {
	                 			    Ext.Array.each(res.tree, function(item){
	                 			    	item.leaf = true;
	                 			    });
	                                record.appendChild(res.tree);
               					}
               				}
           				})
	            	}
				},
				checkchange: function(node, checked){
					var data = node.data,parentNode = node.parentNode;
					var email = node.data.cg_email;
					if(checked){	//选中
						if(data.leaf){	//选中的是叶子节点
							var flag = true;
							parentNode.eachChild(function(child){
								if(!child.data.checked)
									flag = false;
							});
							if(flag)
								parentNode.set('checked',true);
							//将值放到右边收件人
							if(email!=null && email != '')
								appendSpanByCheckbox(email + ';');
						}else{		//选中的不是叶子节点
							//刷新树
							function func(){
								var array = [];
								node.eachChild(function(child){
									if(!child.data.checked){
										child.set('checked',true);
										if(child.data.cg_email != null && child.data.cg_email != '')
											array.push(child.data.cg_email);
									}
								});
								//赋值到右边收件人
								Ext.Array.each(array, function(item){
									appendSpanByCheckbox(item + ';');
								});
							};
							if(node.childNodes == null || node.childNodes == ''){
								Ext.getCmp('group-tree').reloadTree(node,func);
							}else{
								func();
							}
						}
					}else{		//取消选中
						if(data.leaf){		//取消选中的是叶子节点
							parentNode.set('checked',false);
							if(email != null && email != ''){
								deleteSpanByCheckbox(email + ';');
							}
						}else{			//取消选中的不是叶子节点
							node.eachChild(function(child){
								if(child.data.checked)
									child.set('checked',false);
								deleteSpanByCheckbox(child.data.cg_email + ';');
							});
						}
					}
				},
				itemcontextmenu: function(tree, record, item, index, e){
					// 屏蔽浏览器的右键菜单
					e.preventDefault();
					e.stopEvent();
					// 开启自定义右键菜单
					var menu;
					if(!record.data.leaf){	////非叶子节点
						menu = Ext.create('Ext.menu.Menu', {
							items: [{
								text: '新增',
								handler: function(t){
									var tree = Ext.getCmp('group-tree'),
										title = '新增收件人',
										group = group,
										url = 'oa/batchmail/addPersonToGroup.action';
									tree.showFolderAddWin(title,url,record);
								}
							},{
								text: '编辑',
								handler: function(t){
									var tree = Ext.getCmp('group-tree'),
										url = 'oa/batchmail/updateGroupName.action';
									tree.showFolderUpdateWin(record,url);
								}
							},{
								text: '删除',
								handler: function(t){
									Ext.Msg.confirm('提示','确定要删除吗?',function(opt){
										if('yes' == opt){
											//后台删除节点
											Ext.Ajax.request({
												url: basePath + 'oa/batchmail/deleteGroup.action',
												params: {
													group: record.data.text
												},
												success: function(response){
													var res = Ext.decode(response.responseText);
													//前台刷新树
													record.remove();
												}
											});
										}
									});
								}
							}]
						});
						menu.showAt(e.getXY());
					}else{	//叶子节点
						var group = record.data.cg_group;
						menu = Ext.create('Ext.menu.Menu', {
							items: [{
								text: '编辑',
								handler: function(t){
									var tree = Ext.getCmp('group-tree'),
										title = '编辑',
										group = group,
										url = 'oa/batchmail/updatePersonInfo.action';
									tree.showFolderAddWin(title,url,record);
								}
							},{
								text: '删除',
								handler: function(t){
									Ext.Msg.confirm('提示','确定要删除吗?',function(opt){
										if('yes' == opt){
											//后台删除节点
											Ext.Ajax.request({
												url: basePath + 'oa/batchmail/deletePerson.action',
												params: {
													cgId: record.data.cg_id
												},
												success: function(response){
													var res = Ext.decode(response.responseText);
													//前台刷新树
													record.remove();
												}
											});
										}
									});
								}
							}]
						});
						menu.showAt(e.getXY());
					}
				},
				
				
			},
			'field[id=rec_dbfind]': {
				aftertrigger: function(opt,records){
					var array = new Array();
					Ext.Array.each(records,function(record){
						array.push(record.data.cu_email);
					});
					//写入文本域
					Ext.Array.each(array,function(item){
						appendSpanByCheckbox(item+";");
					});
				}
			},
			
			'erpMailContentPanel': {
				afterrender: function(){
					var s = Ext.getCmp('rec_dbfind');
					var btn = document.getElementById('choseButton');
					btn.addEventListener('click', function(){
						Ext.getCmp('rec_dbfind').onTriggerClick();
					});
					/*增加一个div*/
					var div = document.createElement('div');
					div.id = 'autotip';
					document.body.appendChild(div);
					//设置样式
					document.getElementById('reciveman-body').style.border = 'none';
					var input = document.getElementById('must');
					var main = document.getElementById('main'),
						spans = main.childNodes,node;
					input.style.border = 'none';
					input.style.height = '22px';
					//div点击，input获取焦点
					main.style.backgroundColor = 'white';
					main.style.height = '24px';
//					main.style.width = document.getElementById('emailtheme-bodyEl').style.width;
					main.onclick = function(e){
						if(e.target.nodeName != 'SPAN'){
							input.focus();
							removeFocus();
						}
					};
					input.onblur = function(){
						if(input.value != null && input.value != ''){
							appendSpanByInput(null, input);
						}
					}
					//js 键盘事件
					input.onkeypress = function(key){
						if(key.keyCode != 59){		//按下非 ; 键
							removeFocus();		//移除span已选中的样式
						}else{
							//按下;键
							if(input.value != null && input.value != ''){
								appendSpanByInput(key, input);
							}
							//阻止往input输入;
							return false;
						}
					};
					input.onkeyup = function(key){
						doQuery(key, input);
						if(key.keyCode == 8)
							deleteSpanByInput(input);
						else
							return;
					};
					//span点击后按退格删除事件
					document.onkeyup = function(key){
						if(key.keyCode == 8){
							if(input != document.activeElement){
								for(var i = 0; i < spans.length - 1; i++){
									if(spans[i].className == 'span-focus'){
										node = spans[i];
										break;
									}
								}
								if(node != null && node != ''){		//有
									node.remove();
									resizeDiv();	//重置div大小
								}
							}
						}
					};
					
				}
			}
			
		
		});
		function doQuery(key, input){
			var value = input.value;
			var checkbox = Ext.getCmp('searchType');
			var type = checkbox.getValue().type
			if(value){
				Ext.Ajax.request({
					url: basePath + 'oa/batchMail/searchReciveman.action',
					params: {
						value: value,
						type: type
					},
					success: function(response){
						var data = Ext.decode(response.responseText).result;
						var array = [];
						//构造下拉列表
						var tips = document.getElementById('autotip'),
							html = '<ul>';
						if(tips.style.display == 'none')
							tips.style.display = 'inline';
						Ext.Array.each(data, function(item){
							html += '<li>"' + item.name + '" :   ' + item.email + '</li>'
						});
						html += '</ul>';
						tips.innerHTML = html;
						var position = input.getBoundingClientRect(),
							x = position.left,
							y = position.top;
						//设置提示的显示位置
						tips.style.left = x + 'px';
						tips.style.top = y + 23 + 'px';
						var list = tips.getElementsByTagName('li'),
							len = list.length - 1;
						for(var i = 0; i <= len; i++){
							list[i].onmousedown = function(e){
								var str = e.target.textContent;
								str = str.substring(str.indexOf(':')+1).trim();
								appendSpanByCheckbox(str+';');
								input = document.getElementById('must').value = '';		//清空input的值
								tips.style.display = 'none';
							}
						}
						
					}
				});
			}else{
				var tips = document.getElementById('autotip');
				tips.style.display='none';
			}
		}
		function appendSpanByInput(key, input){
			var value = key ? input.value + key.key : input.value+';',
				main = document.getElementById('main'),
				node = document.createElement('span');
			if(main.childNodes.length > 1){		//如果已存在该地址，则不添加
				var flag = true;
				Ext.Array.each(main.childNodes, function(item){
					if(item.tagName == 'SPAN' && item.textContent == value){
						flag = false;
					}
				});
				if(!flag){
					input.value = '';
					return ;
				}
			}
			// 是否合法邮箱
			var reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
			if(reg.test(input.value))
				node.style = 'line-height:22px;float:left;cursor:pointer;';
			else
				node.style = 'line-height:22px;float:left;color:red;cursor:pointer;';
			node.innerText = value;
			main.insertBefore(node, main.childNodes[main.childNodes.length-1]);
			
			node.addEventListener('click',function(){
				//如果存在已经focus的span，移除focus状态
				var spans = main.childNodes;
				for(var i = 0; i < spans.length - 1; i++){
					if(spans[i].className == 'span-focus'){
						spans[i].classList.remove('span-focus');
					}
				}
				node.classList.add('span-focus');
			});
			
			input.value = '';
			resizeDiv();
		}
		function appendSpanByCheckbox(value){
			var main = document.getElementById('main'),
				node = document.createElement('span');
			if(main.childNodes.length > 1){			//如果已存在该地址，则不添加
				var flag = true;
				Ext.Array.each(main.childNodes, function(item){
					if(item.tagName == 'SPAN' && item.textContent == value){
						flag = false;
					}
				});
				if(!flag)
					return ;
			}
			var reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
			if(reg.test(value.replace(';','')))
				node.style = 'line-height:22px;float:left;cursor:pointer;';
			else
				node.style = 'line-height:22px;float:left;color:red;cursor:pointer;';
			node.innerText = value;
			main.insertBefore(node, main.childNodes[main.childNodes.length-1]);
			node.addEventListener('click',function(){
				//如果存在已经focus的span，移除focus状态
				var spans = main.childNodes;
				for(var i = 0; i < spans.length - 1; i++){
					if(spans[i].className == 'span-focus'){
						spans[i].classList.remove('span-focus');
					}
				}
				node.classList.add('span-focus');
			});
			resizeDiv();
		}
		function deleteSpanByCheckbox(value){
			var main = document.getElementById('main'),
				spans = main.childNodes,
				node;
			for(var i = 0; i < spans.length - 1; i++){
				if(spans[i].textContent == value){
					node = spans[i];
					break;
				}
			}
			if(node)
				node.remove();
			resizeDiv();
		}
		function deleteSpanByInput(input){
			var main = document.getElementById('main'),
				spans = main.childNodes,
				node,
				value = input.value;
			if(!value){
				for(var i = 0; i < spans.length - 1; i++){
					if(spans[i].className == 'span-focus'){
						node = spans[i];
						break;
					}
				}
				//是否有选中的span
				if(node != null && node != ''){		//有
					main.removeChild(node);
				}else{
					var lastNode = main.childNodes[main.childNodes.length-2];
					if(lastNode)
						lastNode.classList.add('span-focus');
				}
			}
			resizeDiv();
			//同时将左边树的checked状态改为false
			
		}
		function removeFocus(){
			var main = document.getElementById('main'),
				spans = main.childNodes,
				node;
			for(var i = 0; i < spans.length - 1; i++){
				if(spans[i].className == 'span-focus'){
					node = spans[i];
					break;
				}
			}
			if(node != null && node != ''){		//有
				node.classList.remove('span-focus');
			}
		}
		function resizeDiv(){
			var reciveman = document.getElementById('reciveman'),
				main = document.getElementById('main'),
				input = document.getElementById('must');
			var newHeight = input.offsetTop + 23,
				temp = main.style.height,
				oldHeight = Number(temp.substring(0,temp.indexOf('p')));
			if(newHeight > oldHeight){
				//修改div高度
				reciveman.style.height = newHeight + 10 + 'px';
				main.style.height = newHeight + 'px';
			}else if(newHeight < oldHeight){
				reciveman.style.height = newHeight + 10 + 'px';
				main.style.height = newHeight + 'px';
			}
		}
		
	}
	
});