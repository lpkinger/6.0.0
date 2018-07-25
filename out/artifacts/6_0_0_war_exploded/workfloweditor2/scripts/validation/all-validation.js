//对面板上的流程图进行校验
NodeWrapper = function(node) {
    this.node = node;
    this.id = node.text;

    this.outgoingLinks = [];
    Gef.each(node.outgoingConnections, function(connection) {
        this.outgoingLinks.push(connection.getTarget().text);
    }, this);

    this.incomingLinks = [];
    Gef.each(node.incomingConnections, function(connection) {
        this.incomingLinks.push(connection.getSource().text);
    }, this);

    this.oldInCount = this.incomingLinks.length;
};

NodeWrapper.prototype = {
    isFree: function() {
        return this.node.getType() == 'start'
            || (this.oldInCount > 0 && this.incomingLinks.length == 0);
    },

    isEntry: function() {
        return this.oldInCount > this.incomingLinks.length;
    }
};

Validation = function(editor) {
    this.editor = editor;
    this.processEditPart = editor.getGraphicalViewer().getContents();
    this.processModel = this.processEditPart.getModel();
};

Validation.prototype = {
    validate: function() {
        return this.verifyModel()
            && this.verifySyntax()
            && this.verifyAlone()
            && this.verifySplit();
    },

    // ========================================================================
    verifyModel: function() {
        var result = true;
        Gef.each(this.processModel.children, function(node) {
        	//修改遍历当前的组件的同时 选中当前组件
        	 var viewer = Gef.activeEditor.getGraphicalViewer();
             var browserListener = viewer.getBrowserListener();
             var selectionManager = browserListener.getSelectionManager();
             //selectionManager.selectAll(); 选中所有的  
              selectionManager.selectIn(node);
        	//判断是否是function 如 discision
            if (typeof node.isValid == 'function') {
                result = node.isValid();
                if (result === false) {
                    return false;
                }
            }
        });
        return result;
    },

    // ========================================================================
    verifySyntax: function() {
        return this.verifyStart()
            && this.verifyEnd()
            && this.verifyDuplicateNodeName()
            && this.verifyDuplicateEdge();
    },

    verifyStart: function() {
        var result = true;
        var count = 0;
        Gef.each(this.processModel.children, function(node) {
            if (node.getType() == 'start') {
                if (count > 0) {
                    alert('cannot have more then one START.');
                    result = false;
                    return false;
                }
                if (node.getIncomingConnections().length != 0) {
                    alert('START cannot have any incoming connections.');
                    result = false;
                    return false;
                }
                if (node.getOutgoingConnections().length != 1) {
                    alert('START can have only one outgoing connection.');
                    result = false;
                    return false;
                }
                count++;
            }
        });
        if (count == 0) {
            if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', '必须设置开始节点');
            } else {
                alert('必须设置开始节点');
            }
            result = false;
        }
        return result;
    },

    verifyEnd: function() {
        var count = 0;

        var result = true;
        Gef.each(this.processModel.children, function(node) {
            if (node.getType() == 'end') {
                count++;
                if (node.getOutgoingConnections().length != 0) {
                    alert('END cannot have any outgoing connections.');
                    result = false;
                    return false;
                }
            }
        });
        if (result === false) {
            return false;
        }
        if (count == 0) {
            if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', '必须设置结束节点');
            } else {
                alert('必须设置结束节点');
            }
            return false;
        }

        result = false;
        Gef.each(this.processModel.children, function(node) {
            if (node.getType() == 'end') {
                var ends = node.dom.getAttribute('ends');
                if (Gef.isEmpty(ends) || ends == 'processinstance') {
                    result = true;
                    return false;
                }
            }
        });
        if (result === false) {
            if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', '不能将所有的结束节点的结束方式都设置为execution');
            } else {
                alert('不能将所有的结束节点的结束方式都设置为execution');
            }
        }
        return result;
    },

    verifyDuplicateNodeName: function() {
        var hasEmptyNodeName = false;

        var map = {};
        Gef.each(this.processModel.children, function(sourceNode) {
            var id = sourceNode.text;
            if (Gef.isEmpty(id)) {
                hasEmptyNodeName = true;
                return false;
            }
            if (typeof map[id] == 'undefined') {
                map[id] = 1;
            } else {
                map[id]++;
            }
        });
        if (hasEmptyNodeName) {
            if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', '节点名称不能为空');
            } else {
                alert('节点名称不能为空');
            }
        }

        var info = '节点名称不能重复';
        for (var id in map) {
            var num = map[id];
            if (num > 1) {
                info += '\n ' + id;
            }
        }
        if (info != '节点名称不能重复') {
            if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', info);
            } else {
                alert(info);
            }
            return false;
        } else {
            return true;
        }
    },

    verifyDuplicateEdge: function() {
        var hasDuplicatedEdgeName = false;
        var duplicatedEdgetName = null;
        var duplicatedNodeName = null;

        var map = {};
        Gef.each(this.processModel.children, function(sourceNode) {
            hasDuplicatedEdgeName = false;
            var edgeNames = [];

            Gef.each(sourceNode.getOutgoingConnections(), function(connection) {
                var connectionName = connection.text;
                if (Gef.isEmpty(connectionName)) {
                    connectionName = '';
                }
                if (edgeNames.indexOf(connectionName) == -1) {
                    edgeNames.push(connectionName);
                } else {
                    hasDuplicatedEdgeName = true;
                    duplicatedEdgeName = connectionName;
                    duplicatedNodeName = sourceNode.text;
                    return false;
                }

                var targetNode = connection.getTarget();
                var id = sourceNode.text + ' to ' + targetNode.text;
                if (typeof map[id] == 'undefined') {
                    map[id] = 1;
                } else {
                    map[id]++;
                }
            });

            if (hasDuplicatedEdgeName) {
                return false;
            }
        });

        if (hasDuplicatedEdgeName) {
            if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', duplicatedNodeName + '存在重名的外向连线[' + duplicatedEdgeName + ']');
            } else {
                alert(duplicatedNodeName + '存在重名的外向连线[' + duplicatedEdgeName + ']');
            }
            return false;
        }
       
        //hey 连线的name不能重复  类型不能为空  task节点的连线进出至少要有一条
        var doubleName = false;//重复名称
        var badNode = false;//task节点的连线进出至少要有一条
        var nodeName = '';//问题节点
        var nullType = false;//空类型
        var lineName = '';//问题线名称
        allConnection = new Array();
        allNode = new Array();
        Gef.each(this.processModel.children, function(sourceNode) {
        	if(sourceNode.getTagName()=='task'&&sourceNode.flowtype=='task'){
        		if(sourceNode.incomingConnections.length<1||sourceNode.outgoingConnections.length<1){
        			badNode = true;
        			nodeName = sourceNode.text;
        		}
        	}
        	if(sourceNode.flowtype!="idea"&&sourceNode.flowtype!="derive"&&sourceNode.flowtype!="mission"){//不校验派生节点
        		allNode.push({
            		name : sourceNode.text
            	});//所有的节点
        	}
        	Gef.each(sourceNode.getOutgoingConnections(), function(connection) {
        		if(connection.dom.getAttribute('linetype').length<1){
        			nullType = true;
        			lineName = connection.text;
        		}
        		allConnection.push({
        			name : connection.text,
        			id:    connection.id,
        			source:connection.source.text,
        			tag:connection.target.text
        		});//所有的线段
        	});
    	});
    	if(badNode){
        	if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', 'task节点至少有一条出线和入线 ： '+nodeName);
            } else {
                alert(info);
            }
            return false;
        }
    	if(nullType){
        	if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', '不能有空类型的的线  ： '+lineName);
            } else {
                alert(info);
            }
            return false;
        }
		//比较是否有重名的线
		Gef.each(allConnection, function(connection1) {
			Gef.each(allConnection, function(connection2) {
				if(connection1.id!=connection2.id){
					if(connection1.name==connection2.name){
						doubleName = true;
						lineName = connection1.name;
					}
				}
	    	});
		});
        if(doubleName){
        	if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', '不能有同名的线  ： '+lineName);
            } else {
                alert(info);
            }
            return false;
        }
        var info = '连线不能重复';
        for (var id in map) {
            var num = map[id];
            if (num > 1) {
                info += '\n ' + id;
            }
        }
        if (info != '连线不能重复') {
            if (Gef.notEmpty(Ext)) {
                Ext.Msg.alert('提示', info);
            } else {
                alert(info);
            }
            return false;
        } else {
            return true;
        }
    },

    // ========================================================================
    verifyAlone: function() {
        var result = true;
        var elementsToCheck = {};
        Gef.each(this.processModel.children, function(node) {
            if (node.getType() != 'start'
                && node.getOutgoingConnections().length == 0
                && node.getIncomingConnections().length == 0) {
                result = false;
                return false;
            }
            elementsToCheck[node.text] = new NodeWrapper(node);
        });
        if (result === false) {
            alert('不能包括空组件!');
            return false;
        }

        var count = this.processModel.children;

        while (count > 0) {
            var freeElement = null;
            for (var id in elementsToCheck) {
                var elem = elementsToCheck[id];
                if (elem.isFree()) {
                    freeElement = elem;
                    break;
                }
            }
            if (freeElement == null) {
                var entry = null;
                for (var id in elementsToCheck) {
                    var elem = elementsToCheck[id];
                    if (elem.isEntry()) {
                        entry = elem;
                    }
                }
                if (entry == null) {
                    alert('there is alone node');
                    return false;
                }

                // reverse
                for (var i = 0; i < entry.incomingLinks.length; i++) {
                    var sourceId = entry.id;
                    var targetId = entry.incomingLinks[i];
                    var target = elementsToCheck[targetId];
                    var index = target.outgoingLinks.indexOf(sourceId);
                    target.outgoingLinks.splice(index, 1);
                    target.incomingLinks.push(sourceId);
                }
                entry.incomingLinks = [];
            }

            for (var i = 0; i < freeElement.outgoingLinks.length; i++) {
                var targetId = freeElement.outgoingLinks[i];
                var target = elementsToCheck[targetId];
                var index = target.incomingLinks.indexOf(freeElement.id);
                target.incomingLinks.splice(index, 1);
            }
            delete elementsToCheck[id];
            count--;
        }
        return true;
    },

    // ========================================================================
    verifySplit: function() {
        // TODO
        return true;
    }
};
