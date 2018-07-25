
Ext.ns('App');

App = {
    webforms: {},

    init: function() {
        Ext.QuickTips.init();
        this.propertyManager = new App.property.PropertyManager();

        var viewport = new Ext.Viewport({
            layout: 'border',
            items: [
                this.createNorth(),
                this.createSouth(),
                this.createWest(),
                this.createEast(),
                this.createCenter()
            ]
        });

        this.initEditor();

        setTimeout(function() {
            Ext.get('loading').remove();
            Ext.get('loading-mask').fadeOut({remove:true});
        }, 100);
    },

    initEditor: function() {
        var xml = "<?xml version='1.0' encoding='UTF-8'?>"
            + "<process></process>";

        var editor = new Gef.jbs.ExtEditor();
        var input = new Gef.jbs.JBSEditorInput();
        input.readXml(xml);

        var workbenchWindow = new Gef.ui.support.DefaultWorkbenchWindow();
        workbenchWindow.getActivePage().openEditor(editor, input);

        workbenchWindow.render();

        Gef.activeEditor = editor;

        this.propertyManager.initSelectionListener(editor);
    },

    getProcessModel: function() {
        var viewer = Gef.activeEditor.getGraphicalViewer();
        var processEditPart = viewer.getContents();
        return processEditPart.model;
    },

    createNorth: function() {
        var p = null;
        if (Gef.MODE_DEMO === true) {
            p = new Ext.Panel({
                region: 'north'
            });
        } else {
            p = new Ext.Panel({
                region: 'north',
                html: '<h1 id="pageh1">Web户型图设计器 - 多比图形控件演示<h1>'
            });
        }

        App.northPanel = p;
        return p;
    },

    createSouth: function() {
        var p = this.propertyManager.getBottom();
        return p;
    },

    createWest: function() {
        var p = new App.PalettePanel({
            collapsible: true
        });

        App.westPanel = p;
        return p;
    },

    createEast: function() {
        var p = this.propertyManager.getRight();
        return p;
    },

    createCenter: function() {
        var p = new App.CanvasPanel();

        App.centerPanel = p;
        return p;
    },

    getSelectionListener: function() {
        if (!this.selectionListener) {
            this.selectionListener = new Gef.jbs.ExtSelectionListener(null);
        }
        return this.selectionListener;
    }
};

/*
Gef.override(App.PalettePanel, {
    configItems: function() {
        this.html = 'sdfasfdfdsa';
    }
});
*/

//Gef.PALETTE_TYPE = 'plain';
Gef.PALETTE_TYPE = 'accordion';

Ext.onReady(App.init, App);



App.CanvasPanel = Ext.extend(Ext.Panel, {
    initComponent: function() {
        //this.on('bodyresize', function(p, w, h) {
        //    var b = p.body.getBox();
        //});
        this.region = 'center';
        this.autoScroll = true;
        this.tbar = new Ext.Toolbar([{
            text: '新建',
            iconCls: 'tb-new',
            handler: function() {
                Gef.activeEditor.reset();
            }
        }, {
            text: '导入',
            iconCls: 'tb-webform',
            handler: function() {
                var xml = Gef.activeEditor.serial();
                if (!this.openWin) {
                    this.openWin = new Ext.Window({
                        title: 'xml',
                        layout: 'fit',
                        width: 500,
                        height: 300,
                        closeAction: 'hide',
                        modal: true,
                        items: [{
                            id: '__gef_jbpm4_xml_import__',
                            xtype: 'textarea'
                        }],
                        buttons: [{
                            text: '导入',
                            handler: function() {
                                var xml = Ext.getDom('__gef_jbpm4_xml_import__').value;
                                Gef.activeEditor.resetAndOpen(xml);
                                this.openWin.hide();
                            },
                            scope: this
                        }, {
                            text: '取消',
                            handler: function() {
                                this.openWin.hide();
                            },
                            scope: this
                        }]
                    });
                    this.openWin.on('show', function() {
                        Gef.activeEditor.disable();
                    });
                    this.openWin.on('hide', function() {
                        Gef.activeEditor.enable();
                    });
                }
                this.openWin.show(null, function() {
                    Ext.getDom('__gef_jbpm4_xml_import__').value = xml;
                });
            }
        }, {
            text: '导出',
            iconCls: 'tb-prop',
            handler: function() {
                var xml = Gef.activeEditor.serial();
                if (!this.openWin) {
                    this.openWin = new Ext.Window({
                        title: 'xml',
                        layout: 'fit',
                        width: 500,
                        height: 300,
                        closeAction: 'hide',
                        modal: true,
                        items: [{
                            id: '__gef_jbpm4_xml_export__',
                            xtype: 'textarea'
                        }],
                        buttons: [{
                            text: '关闭',
                            handler: function() {
                                this.openWin.hide();
                            },
                            scope: this
                        }]
                    });
                    this.openWin.on('show', function() {
                        Gef.activeEditor.disable();
                    });
                    this.openWin.on('hide', function() {
                        Gef.activeEditor.enable();
                    });
                }
                this.openWin.show(null, function() {
                    Ext.getDom('__gef_jbpm4_xml_export__').value = xml;
                });
            }
        }, {
            text: '保存',
            iconCls: 'tb-save',
            handler: function() {
                var editor = Gef.activeEditor;
                var xml = editor.serial();
                var name = editor.getGraphicalViewer().getContents().getModel().text;
                Ext.Msg.wait('正在保存');
                Ext.Ajax.request({
                    method: 'post',
                    url: Gef.SAVE_URL,
                    success: function(response) {
                        try {
                            var o = Ext.decode(response.responseText);
                            if (o.success === true) {
                                Ext.Msg.alert('信息', '操作成功');
                            } else {
                                Ext.Msg.alert('错误', o.errors.msg);
                            }
                        } catch(e) {
                            Ext.Msg.alert('系统错误', response.responseText);
                        }
                    },
                    failure: function(response) {
                        Ext.Msg.alert('系统错误', response.responseText);
                    },
                    params: {
                        id: Gef.PROCESS_ID,
                        processName: name,
                        xml: xml
                    }
                });
            }
        }, {
            text: '发布',
            iconCls: 'tb-deploy',
            handler: function() {
                var editor = Gef.activeEditor;

                var isValid = new Validation(editor).validate();
                if (!isValid) {
                    return false;
                }

                var xml = editor.serial();
                var model = editor.getGraphicalViewer().getContents().getModel();
                var name = model.text;
                Ext.Msg.wait('正在发布');
                Ext.Ajax.request({
                    method: 'post',
                    url: Gef.DEPLOY_URL,
                    success: function(response) {
                        try {
                            var o = Ext.decode(response.responseText);
                            if (o.success === true) {
                                Ext.Msg.alert('信息', '操作成功');
                            } else {
                                Ext.Msg.alert('错误', o.errors.msg);
                            }
                        } catch(e) {
                            Ext.Msg.alert('系统错误', response.responseText);
                        }
                    },
                    failure: function(response) {
                        Ext.Msg.alert('系统错误', response.responseText);
                    },
                    params: {
                        id: Gef.PROCESS_ID,
                        procCatId: model.procCatId,
                        procDefName: model.procDefName,
                        procDefCode: model.procDefCode,
                        procVerName: model.procVerName,
                        xml: xml
                    }
                });
            }
        }, {
            text: '清空',
            iconCls: 'tb-clear',
            handler: function() {
                Gef.activeEditor.clear();
            }
        }, {
            text: '撤销',
            iconCls: 'tb-undo',
            handler: function() {
                var viewer = Gef.activeEditor.getGraphicalViewer();
                var browserListener = viewer.getBrowserListener();
                var selectionManager = browserListener.getSelectionManager();
                selectionManager.clearAll();
                var commandStack = viewer.getEditDomain().getCommandStack();
                commandStack.undo();
            },
            scope: this
        }, {
            text: '重做',
            iconCls: 'tb-redo',
            handler: function() {
                var viewer = Gef.activeEditor.getGraphicalViewer();
                var browserListener = viewer.getBrowserListener();
                var selectionManager = browserListener.getSelectionManager();
                selectionManager.clearAll();
                var commandStack = viewer.getEditDomain().getCommandStack();
                commandStack.redo();
            },
            scope: this
        }, {
            text: '布局',
            iconCls: 'tb-activity',
            handler: function() {

                var viewer = Gef.activeEditor.getGraphicalViewer();
                var browserListener = viewer.getBrowserListener();
                var selectionManager = browserListener.getSelectionManager();
                selectionManager.clearAll();

                new Layout(Gef.activeEditor).doLayout();
            },
            scope: this
        }, {
            text: '删除',
            iconCls: 'tb-delete',
            handler: this.removeSelected,
            scope: this
        }]);

        App.CanvasPanel.superclass.initComponent.call(this);
    },

    afterRender: function() {
        App.CanvasPanel.superclass.afterRender.call(this);

        var width = 1500;
        var height = 1000;

        Ext.DomHelper.append(this.body, [{
            id: '__gef_jbs__',
            tag: 'div',
            style: 'width:' + (width + 10) + 'px;height:' + (height + 10) + 'px;',
            children: [{
                id: '__gef_jbs_center__',
                tag: 'div',
                style: 'width:' + width + 'px;height:' + height + 'px;float:left;'
            }, {
                id: '__gef_jbs_right__',
                tag: 'div',
                style: 'width:10px;height:' + height + 'px;float:left;background-color:#EEEEEE;cursor:pointer;'
            }, {
                id: '__gef_jbs_bottom__',
                tag: 'div',
                style: 'width:' + (width + 10) + 'px;height:10px;float:left;background-color:#EEEEEE;cursor:pointer;'
            }]
        }]);

        var rightEl = Ext.fly('__gef_jbs_right__');
        rightEl.on('mouseover', function(e) {
            var t = e.getTarget();
            t.style.backgroundColor = 'yellow';
            t.style.backgroundImage = 'url(images/arrow/arrow-right.png)';
        });
        rightEl.on('mouseout', function(e) {
            var t = e.getTarget();
            t.style.backgroundColor = '#EEEEEE';
            t.style.backgroundImage = '';
        });
        rightEl.on('click', function(e) {
            Ext.fly('__gef_jbs__').setWidth(Ext.fly('__gef_jbs__').getWidth() + 100);
            Ext.fly('__gef_jbs_center__').setWidth(Ext.fly('__gef_jbs_center__').getWidth() + 100);
            Ext.fly('__gef_jbs_bottom__').setWidth(Ext.fly('__gef_jbs_bottom__').getWidth() + 100);

            Gef.activeEditor.addWidth(100);
        });

        var bottomEl = Ext.fly('__gef_jbs_bottom__');
        bottomEl.on('mouseover', function(e) {
            var t = e.getTarget();
            t.style.backgroundColor = 'yellow';
            t.style.backgroundImage = 'url(images/arrow/arrow-bottom.png)';
        });
        bottomEl.on('mouseout', function(e) {
            var t = e.getTarget();
            t.style.backgroundColor = '#EEEEEE';
            t.style.backgroundImage = '';
        });
        rightEl.on('click', function(e) {
            Ext.fly('__gef_jbs__').setHeight(Ext.fly('__gef_jbs__').getHeight() + 100);
            Ext.fly('__gef_jbs_center__').setHeight(Ext.fly('__gef_jbs_center__').getHeight() + 100);
            Ext.fly('__gef_jbs_right__').setHeight(Ext.fly('__gef_jbs_right__').getHeight() + 100);

            Gef.activeEditor.addHeight(100);
        });

        this.body.on('contextmenu', this.onContextMenu, this);
    },

    onContextMenu: function(e) {
        if (!this.contextMenu) {
            this.contextMenu = new Ext.menu.Menu({
                items: [{
                    text: '详细配置',
                    iconCls: 'tb-prop',
                    handler: this.showWindow,
                    scope: this
                }, {
                    text: '删除',
                    iconCls: 'tb-remove',
                    handler: this.removeSelected,
                    scope: this
                }]
            });
        }
        e.preventDefault();
        this.contextMenu.showAt(e.getXY());
    },

    showWindow: function() {
        App.propertyManager.changePropertyStatus('max');
    },

    removeSelected: function() {
        var viewer = Gef.activeEditor.getGraphicalViewer();
        var browserListener = viewer.getBrowserListener();
        var selectionManager = browserListener.getSelectionManager();

        var edge = selectionManager.selectedConnection;
        var nodes = selectionManager.items;

        var request = {};

        if (edge != null) {
            request.role = {
                name: 'REMOVE_EDGE'
            };
            this.executeCommand(edge, request);
            selectionManager.removeSelectedConnection();
        } else if (nodes.length > 0) {
            request.role = {
                name: 'REMOVE_NODES',
                nodes: nodes
            };
            this.executeCommand(viewer.getContents(), request);
            selectionManager.clearAll();
        }
    },

    executeCommand: function(editPart, request) {
        var command = editPart.getCommand(request);
        if (command != null) {
            Gef.activeEditor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
        }
    }
});

/*
 * Compressed by JSA(www.xidea.org)
 */

Layout = function(editor) {
    this.editor = editor;
    this.processEditPart = editor.getGraphicalViewer().getContents();
    this.processModel = this.processEditPart.getModel();
};

Layout.prototype = {
    doLayout: function() {
        var diagram = new Diagram();
        diagram.init(this.processModel);

        var sorter = new TopologicalSorter(diagram);
        var sortedElements = sorter.getSortedElements();
        var sortedIds = [];
        for (var i = 0; i < sortedElements.length; i++) {
            sortedIds.push(sortedElements[i].id);
        }

        //console.info(sortedIds);

        var layouter = new LeftToRightGridLayouter(diagram, sortedIds);
        layouter.doLayout();

        var edgeMap = diagram.getEdgeMap();
        for (var edgeId in edgeMap) {
            var edge = edgeMap[edgeId];
            new EdgeLayouter(layouter.grid, edge);
        }

        diagram.updateModel();
    }
};

;

Diagram = function() {
};

Diagram.prototype = {
    getNodeMap: function() {
        return this.nodeMap;
    },

    getEdgeMap: function() {
        return this.edgeMap;
    },

    init: function(process) {
        this.process = process;
        this.nodeMap = {};
        this.edgeMap = {};

        for (var i = 0; i < this.process.children.length; i++) {
            var child = process.children[i];
            var node = new Node();
            node.id = child.text;
            node.type = child.type;
            node.x = child.x;
            node.y = child.y;
            node.w = child.w;
            node.h = child.h;

            this.nodeMap[node.id] = node;
        }

        for (var i = 0; i < this.process.children.length; i++) {
            var child = process.children[i];

            for (var j = 0; j < child.getOutgoingConnections().length; j++) {
                var connection = child.getOutgoingConnections()[j];
                this.createEdge(connection);
            }
        }
    },

    createEdge: function(connection) {
        var connectionId = connection.getSource().text + '_' + connection.getTarget().text;
        var edge = this.edgeMap[connectionId];
        if (!edge) {
            edge = new Edge();
            edge.id = connectionId;
            edge.name = connection.text;
            edge.source = this.nodeMap[connection.getSource().text];
            edge.target = this.nodeMap[connection.getTarget().text];

            edge.source.outgoingLinks.push(edge);
            edge.target.incomingLinks.push(edge);

            this.edgeMap[connectionId] = edge;
        }
    },

    updateModel: function() {
        for (var nodeId in this.nodeMap) {
            var item = this.nodeMap[nodeId];
            var model = this.getModel(nodeId);
            model.x = item.x;
            model.y = item.y;

            model.getEditPart().getFigure().x = model.x;
            model.getEditPart().getFigure().y = model.y;

            for (var i = 0; i < item.outgoingLinks.length; i++) {
                var connection = item.outgoingLinks[i];

                var connectionModel = this.getConnectionModel(model, connection);
                if (connectionModel == null) {
                    continue;
                }

                connectionModel.innerPoints = typeof connection.innerPoints == 'undefined' ? []
                                                : connection.innerPoints;
                connectionModel.textX = 0;
                connectionModel.textY = 0;
                connectionModel.getEditPart().getFigure().innerPoints = connectionModel.innerPoints;
                connectionModel.getEditPart().getFigure().textX = connectionModel.textX;
                connectionModel.getEditPart().getFigure().textY = connectionModel.textY;
            }
        }
        this.process.getEditPart().refresh();
    },

    getModel: function(name) {
        var model = null;

        Gef.each(this.process.children, function(item) {
            if (item.text == name) {
                model = item;
                return false;
            }
        });

        return model;
    },

    getConnectionModel: function(nodeModel, edge) {
        var model = null;

        Gef.each(nodeModel.getOutgoingConnections(), function(item) {
            if (item.getTarget().text == edge.getTarget().id) {
                model = item;
                return false;
            }
        });

        return model;
    }
};

;

// should be change name to LayoutingElement
Node = function() {
    this.incomingLinks = [];
    this.outgoingLinks = [];
};

Node.prototype = {
    getIncomingLinks: function() {
        return this.incomingLinks;
    },

    getOutgoingLinks: function() {
        return this.outgoingLinks;
    },

    getPrecedingElements: function() {
        var previousElements = [];
        for (var i = 0; i < this.incomingLinks.length; i++) {
            previousElements.push(this.incomingLinks[i].source);
        }
        return previousElements;
    },

    getFollowingElements: function() {
        var followingElements = [];
        for (var i = 0; i < this.outgoingLinks.length; i++) {
            followingElements.push(this.outgoingLinks[i].target);
        }
        return followingElements;
    },

    isJoin: function() {
        return this.incomingLinks.length > 1;
    },

    isSplit: function() {
        return this.outgoingLinks.length > 1;
    },

    prevSplit: function() {
        var distance = 1000;
        var candidateDistance = 0;
        var split = null;
        var candidate = null;

        var precedingElements = this.getPrecedingElements();
        for (var i = 0; i < precedingElements.length; i++) {
            var elem = precedingElements[i];
            if (elem.isSplit()) {
                return elem;
            }

            candidate = elem.prevSplit();
            if (this.isJoin()) {
                // if this is not a join, we have only one precedingElement.
                candidateDistance = elem.backwardDistanceTo(candidate);
            }
            if (candidateDistance < distance) {
                split = candidate;
                distance = candidateDistance;
            }
        }
        return split;
    },

    backwardDistanceTo: function(other) {
        return this._backwardDistanceTo(other, []);
    },

    _backwardDistanceTo: function(other, historyElements) {
        if (other == this) {
            return 0;
        }
        if (historyElements.indexOf(this) != -1) {
            return 1000;
        }
        var d = 1000;
        var newHistory = [];
        newHistory.push(this);
        var precedingElements = this.getPrecedingElements();
        for (var i = 0; i < precedingElements.length; i++) {
            var el = precedingElements[i];
            d = Math.min(d, el._backwardDistanceTo(other, newHistory));
        }
        return d == 1000 ? d : d + 1;
    }
};

;

Edge = function() {
    this.source = null;
    this.target = null;
};

Edge.prototype = {
    getSource: function() {
        return this.source;
    },

    getTarget: function() {
        return this.target;
    },

    reverseOutgoingAndIncoming: function() {

        var index = 0;

        var oldSource = this.source;
        var oldTarget = this.target;

        index = oldSource.outgoingLinks.indexOf(this);

        oldSource.outgoingLinks.splice(index, 1);

        index = oldTarget.incomingLinks.indexOf(this);
        oldTarget.incomingLinks.splice(index, 1);

        var newSource = oldTarget;
        var newTarget = oldSource;

        newSource.outgoingLinks.push(this);
        newTarget.incomingLinks.push(this);

        this.source = newSource;
        this.target = newTarget;

    }
};

;

TopologicalSorter = function(diagram) {
    this.diagram = diagram;
    this.prepareDataAndSort(true);
    this.prepareDataAndSort(false);
};

TopologicalSorter.prototype = {
    getSortedElements: function() {
        return this.sortedElements;
    },

    prepareDataAndSort: function(shouldBackpatch) {
        this.sortedElements = [];
        this.elementsToSort = {};
        this.backwardsEdges = [];
        this.elementsToSortCount = 0;

        this.addAllChildren();
        this.topologicalSort();
        if (shouldBackpatch === true) {
            this.backpatchBackwardsEdges();
        }

        this.reverseBackwardsEdges();
    },

    addAllChildren: function() {
        for (var nodeId in this.diagram.nodeMap) {
            var node = this.diagram.nodeMap[nodeId];
            this.elementsToSort[nodeId] = new SortableLayoutingElement(node);
            this.elementsToSortCount++;
        }
    },

    topologicalSort: function() {
        var count = 0;
        var oldCount = 0;
        while (this.elementsToSortCount > 0) {
            var freeElements = this.getFreeElements();

            if (freeElements.length > 0) {

                for (var i = 0; i < freeElements.length; i++) {
                    var freeElement = freeElements[i];
                    this.sortedElements.push(freeElement.node);
                    this.freeElementsFrom(freeElement);
                    delete this.elementsToSort[freeElement.node.id];
                }
            } else {

                var entry = this.getLoopEntryPoint();

                for (var i = 0; i < entry.incomingLinks.length; i++) {
                    var backId = entry.incomingLinks[i];
                    entry.reverseIncomingLinkFrom(backId);
                    var elem = this.elementsToSort[backId];
                    elem.reverseOutgoingLinkTo(entry.node.id);

                    this.backwardsEdges.push(new BackwardsEdge(backId, entry.node.id));
                }
            }
        }
    },

    backpatchBackwardsEdges: function() {
        var newBackwardsEdges = [];
        for (var i = 0; i < this.backwardsEdges.length; i++) {
            newBackwardsEdges.push(this.backwardsEdges[i]);
        }
        for (var i = 0; i < this.backwardsEdges.length; i++) {
            var edge = this.backwardsEdges[i];
            var sourceId = edge.getSource();
            var targetId = edge.getTarget();

            var sourceElement = this.diagram.nodeMap[sourceId];

            while (!(sourceElement.isJoin() || sourceElement.isSplit())) {
                var newSourceElement = sourceElement.getPrecedingElements()[0];

                targetId = newSourceElement.id;
                newBackwardsEdges.push(new BackwardsEdge(targetId, sourceId));

                sourceElement = newSourceElement;
                sourceId = targetId;
            }
        }

        this.backwardsEdges = newBackwardsEdges;
    },

    reverseBackwardsEdges: function() {
        var edgeMap = this.diagram.edgeMap;
        for (var i = 0; i < this.backwardsEdges.length; i++) {
            var backwardsEdge = this.backwardsEdges[i];

            var sourceId = backwardsEdge.getSource();
            var targetId = backwardsEdge.getTarget();

            var sourceElement = this.diagram.nodeMap[sourceId];
            var targetElement = this.diagram.nodeMap[targetId];

            var edge = this.getEdge(edgeMap, sourceElement, targetElement);

            backwardsEdge.setEdge(edge);

            if (edge) {
                // reverse edge outgoing and incoming
                edge.reverseOutgoingAndIncoming();
            }
        }
    },

    getFreeElements: function() {
        var freeElements = [];
        for (var nodeId in this.elementsToSort) {
            var elem = this.elementsToSort[nodeId];
            if (elem.isFree()) {
                freeElements.push(elem);
            }
        }
        return freeElements;
    },

    freeElementsFrom: function(freeElement) {
        for (var i = 0; i < freeElement.outgoingLinks.length; i++) {
            var id = freeElement.outgoingLinks[i];
            var targetElement = this.elementsToSort[id];
            if (targetElement) {
                targetElement.removeIncomingLinkFrom(freeElement.node.id);
            }
        }
        this.elementsToSortCount--;
    },

    getLoopEntryPoint: function() {
        for (var nodeId in this.elementsToSort) {
            var candidate = this.elementsToSort[nodeId];
            if (candidate.oldInCount > 1
                    && candidate.oldInCount > candidate.incomingLinks.length) {
                return candidate;
            }
        }
        throw new Error('Could not find a valid loop entry point');
    },

    getEdge: function(edgeMap, sourceElement, targetElement) {
        for (var i = 0; i < sourceElement.outgoingLinks.length; i++) {
            var edge = sourceElement.outgoingLinks[i];
            if (edge.getTarget().id == targetElement.id) {
                return edge;
            }
        }
        return null;
    }
};

;

SortableLayoutingElement = function(node) {
    this.node = node;
    this.incomingLinks = [];
    this.outgoingLinks = [];

    for (var i = 0; i < node.incomingLinks.length; i++) {
        this.incomingLinks.push(node.incomingLinks[i].source.id);
    }
    for (var i = 0; i < node.outgoingLinks.length; i++) {
        this.outgoingLinks.push(node.outgoingLinks[i].target.id);
    }

    this.oldInCount = this.incomingLinks.length;
    this.isJoin = node.isJoin();
};

SortableLayoutingElement.prototype = {
    isFree: function() {
        return this.incomingLinks.length == 0;
    },

    removeIncomingLinkFrom: function(sourceId) {
        var index = this.incomingLinks.indexOf(sourceId);
        this.incomingLinks.splice(index, 1);
    },

    reverseIncomingLinkFrom: function(id) {
        this.removeIncomingLinkFrom(id);
        this.outgoingLinks.push(id);
    },

    reverseOutgoingLinkTo: function(id) {
        var index = this.outgoingLinks.indexOf(id);
        this.outgoingLinks.splice(index, 1);
        this.incomingLinks.push(id);
    }
};

;

BackwardsEdge = function(source, target) {
    this.source = source;
    this.target = target;
};

BackwardsEdge.prototype = {
    getEdge: function() {
        return this.edge;
    },

    setEdge: function(edge) {
        this.edge = edge;
    },

    getSource: function() {
        return this.source;
    },

    getTarget: function() {
        return this.target;
    }
};

;

LeftToRightGridLayouter = function(diagram, sortedIds) {
    this.diagram = diagram;
    this.sortedIds = sortedIds;
};

LeftToRightGridLayouter.prototype = {
    doLayout: function() {
        this.grid = new Grid();

        this.layoutElements();

        this.calcGeometry(this.grid);
        this.writeGeometry(this.grid);

        this.diagram.updateModel();
    },

    layoutElements: function() {
        for (var i = 0; i < this.sortedIds.length; i++) {
            var sortedId = this.sortedIds[i];
            var currentElement = this.diagram.nodeMap[sortedId];
            var precedingElements = currentElement.getPrecedingElements();

            var cellOfElement = this.placeElement(currentElement, precedingElements);

            if (currentElement.isJoin() && precedingElements.length != 0) {
                // there is an edge hitting us left, so lets forbid
                // interleaving to use the left cell, if this is empty
                cellOfElement.getPrevCell().setPackable(false);
            }

            if (currentElement.isSplit()) {
                this.prelayoutSuccessors(currentElement, cellOfElement);
            }
        }
    },

    placeElement: function(currentElement, precedingElements) {
        var newCell = null;

        if (precedingElements.length == 0) {
            this.grid.startCell.value = currentElement;
            newCell = this.grid.startCell;
        } else {
            var leftCell = null;
            var newCell = this.grid.getCellOfItem(currentElement);

            if (currentElement.isJoin()) {
                var splitFound = false;

                var split = currentElement.prevSplit();
                if (split != null) {
                    var splits = new PriorityQueue(currentElement);
                    splits.add(split);
                    for (var i = 0; i < precedingElements.length; i++) {
                        var elem = precedingElements[i];
                        split = elem.prevSplit();
                        if (split != null && !splits.contains(split)) {
                            splits.add(split);
                            //console.info(splits.items);
                        }
                    }
                    split = null;
                    // get split with most connections
                    var maxCon = 0;
                    for (var i = 0; i < splits.items.length; i++) {
                        var target = splits.items[i];
                        if (target == currentElement) {
                            continue;
                        }
                        // current connections
                        var curCon = 0;
                        for (var j = 0; j < precedingElements.length; j++) {
                            var elem = precedingElements[j];
                            if (elem.backwardDistanceTo(target) < 1000) {
                                curCon++;
                            }
                        }
                        if (curCon > maxCon) {
                            maxCon = curCon;
                            split = target;
                        }
                    }
                    splitFound = split != null;
                }

                // current cell position
                var x = 0;
                var yAcc = 0;
                var yCnt = 0;
                for (var i = 0; i < precedingElements.length; i++) {
                    var elem = precedingElements[i];
                    var tmp = this.grid.getCellOfItem(elem);

                    if (tmp == null) {
                        tmp = {
                            getColIndex: function() {
                                return 0;
                            }
                        };
                    } else {
                        yAcc += tmp.getRowIndex();
                        yCnt++;
                    }
                    x = Math.max(x, tmp.getColIndex());
                }

                // farthest to the right
                if (splitFound) {
                    leftCell = this.grid.getCellOfItem(split).row.cells[x];

                    // set path to split unpackable
                    for (var c = leftCell; c.value != split; c = c.getPrevCell()) {
                        c.setPackable(false);
                    }
                } else {
                    if (yCnt == 0) {
                        leftCell = this.grid.rows[0].above().cells[x];
                    } else {
                        leftCell = this.grid.rows[yAcc / yCnt].cells[x];
                    }
                }

                if (newCell != null && newCell.value == currentElement) {
                    newCell.value = null;
                }
                newCell = leftCell.after();

                // set all incoming pathes unpackable

                for (var i = 0; i < precedingElements.length; i++) {
                    var el = precedingElements[i];
                    var target = this.grid.getCellOfItem(el);
                    if (target == null) {
                        continue;
                    }

                    var start = target.row.cells[x + 1];
                    for (var c = start; c != target; c = c.getPrevCell()) {
                        c.setPackable(false);
                    }
                }

            } else if (newCell == null) {
                // if not preLayouted
                var preElem = precedingElements[0];
                leftCell = this.grid.getCellOfItem(preElem);

                newCell = leftCell.after();
            }

            if (newCell.isFilled() && newCell.value != currentElement) {
                newCell.row.insertRowBeneath();
                newCell = newCell.beneath();
            }
            newCell.value = currentElement;
            newCell.getPrevCell().setPackable(false);
        }

        return newCell;
    },

    prelayoutSuccessors: function(currentElement, cellOfElement) {
        var baseCell = cellOfElement.after();
        var topCell = baseCell;
        var followingElements = currentElement.getFollowingElements();

        // heuristic for direct connection to join
        var directJoin = null;
        for (var i = 0; i < followingElements.length; i++) {
            var possibleJoin = followingElements[i];
            if (possibleJoin.isJoin()) {
                directJoin = possibleJoin;
            }
        }
        if (directJoin != null) {
            // put in the middle
            var index = followingElements.indexOf(directJoin);
            // remove
            followingElements.splice(index, 1);
            var position = parseInt(followingElements.length / 2, 10);
            // insert
            followingElements.splice(position, 0, directJoin);
        }

        // normal preLayout following elements
        var follow = parseInt(followingElements.length / 2, 10);
        for (var i = 0; i < follow; i++) {
            topCell.row.insertRowAbove();
            baseCell.row.insertRowBeneath();
            topCell = topCell.above();
        }

        for (var i = 0; i < followingElements.length; i++) {
            var newElem = followingElements[i];

            if (this.grid.getCellOfItem(newElem)) {
                continue;
            }

            topCell.value = newElem;
            topCell = topCell.beneath();
            if (topCell == baseCell && followingElements.length % 2 == 0) {
                topCell = topCell.beneath();
            }
        }
    },

    calcGeometry: function(grid) {
        grid.pack();

        var heightOfRow = [];
        for (var i = 0; i < grid.rowCount; i++) {
            heightOfRow.push(0);
        }
        var widthOfColumn = [];
        for (var i = 0; i < grid.colCount; i++) {
            widthOfColumn.push(0);
        }

        for (var i = 0; i < grid.rowCount; i++) {
            var row = grid.rows[i];

            for (var j = 0; j < grid.colCount; j++) {
                var cell = row.cells[j];
                if (cell.isFilled()) {
                    var elem = cell.value;
                    widthOfColumn[j] = Math.max(widthOfColumn[j], elem.w + 30);
                    heightOfRow[i] = Math.max(heightOfRow[i], elem.h + 30);
                }
            }
        }

        this.heightOfRow = heightOfRow;
        this.widthOfColumn = widthOfColumn;

        this.totalWidth = 0;
        this.totalHeight = 0;
        for (var i = 0; i < grid.colCount; i++) {
            this.totalWidth += widthOfColumn[i];
        }
        for (var i = 0; i < grid.rowCount; i++) {
            this.totalHeight += heightOfRow[i];
        }
    },

    writeGeometry: function(grid) {
        var x = 0;
        var y = 0;

        for (var i = 0; i < grid.rowCount; i++) {
            var row = grid.rows[i];

            var cellHeight = this.heightOfRow[i];

            for (var j = 0; j < grid.colCount; j++) {
                var cell = row.cells[j];

                var cellWidth = this.widthOfColumn[j];

                if (cell.isFilled()) {
                    var elem = cell.value;

                    var newX = x + cellWidth / 2 - elem.w / 2;
                    var newY = y + cellHeight / 2 - elem.h / 2;

                    elem.x = newX;
                    elem.y = newY;
                }
                x += cellWidth;
            }
            x = 0;
            y += cellHeight;
        }
    }
};

;

Grid = function() {
    var cell = new Cell();

    var row = new Row();
    row.grid = this;
    row.addCell(cell);

    this.rows = [row];

    this.startCell = cell;

    this.colCount = 1;
    this.rowCount = this.rows.length;
};

Grid.prototype = {
    addFirstRow: function() {
        var row = new Row();
        row.grid = this;
        for (var i = 0; i < this.rolCount; i++) {
            var cell = new Cell();
            row.addCell(cell);
        }
        this.rows.unshift(row);

        this.rowCount = this.rows.length;
    },

    addLastRow: function() {
        var row = new Row();
        row.grid = this;
        for (var i = 0; i < this.rolCount; i++) {
            row.addCell(new Cell());
        }
        this.rows.push(row);

        this.rowCount = this.rows.length;
    },

    addLastCol: function() {
        for (var i = 0; i < this.rowCount; i++) {
            var row = this.rows[i];

            row.addCell(new Cell());
        }

        this.colCount++;
    },

    getCellOfItem: function(elem) {
        for (var i = 0; i < this.rowCount; i++) {
            var row = this.rows[i];
            for (var j = 0; j < this.colCount; j++) {
                var cell = row.cells[j];
                if (cell.value == elem) {
                    return cell;
                }
            }
        }
        return null;
    },

    pack: function() {
        var changed = false;
        do {
            changed = false;
            for (var i = 0; i < this.rows.length; i++) {
                var row = this.rows[i];
                changed |= row.tryInterleaveWith(row.getPrevRow());
            }
            for (var i = 0; i < this.rows.length; i++) {
                var row = this.rows[i];
                changed |= row.tryInterleaveWith(row.getNextRow());
            }
        } while (changed);
    },

    info: function() {
        var value = '';
        for (var i = 0; i < this.rows.length; i++) {
            var row = this.rows[i];
            for (var j = 0; j < row.cells.length; j++) {
                var cell = row.cells[j];
                var id = '[    ]';
                if (cell.isFilled()) {
                    id = cell.value.id;
                } else if (cell.packable === false) {
                    id = '[ p  ]';
                }
                value += id;
            }
            value += '\n'
        }
        return value;
    }
};

;

Cell = function() {
    this.packable = true;
};

Cell.prototype = {
    isFilled: function() {
        return typeof this.value != 'undefined' && this.value != null;
    },

    isUnpackable: function() {
        return this.isFilled() || (this.packable === false);
    },

    setPackable: function(packable) {
        this.packable = packable;
    },

    getRowIndex: function() {
        return this.row.getIndex();
    },

    getColIndex: function() {
        for (var i = 0; i < this.row.cells.length; i++) {
            if (this.row.cells[i] == this) {
                return i;
            }
        }
    },

    after: function() {
        var colIndex = this.getColIndex();
        if (colIndex == this.row.cells.length - 1) {
            this.grid.addLastCol();
        }
        return this.row.cells[colIndex + 1];
    },

    above: function() {
        var rowIndex = this.getRowIndex();
        var colIndex = this.getColIndex();
        if (rowIndex == 0) {
            this.row.insertRowAbove();
        }
        return this.grid.rows[rowIndex - 1].cells[colIndex];
    },

    beneath: function() {
        var rowIndex = this.getRowIndex();
        var colIndex = this.getColIndex();
        if (rowIndex == this.grid.rowCount - 1) {
            this.row.insertRowBeneath();
        }
        return this.grid.rows[rowIndex + 1].cells[colIndex];
    },

    getPrevCell: function() {
        var index = this.getColIndex();
        return this.row.cells[index - 1];
    },

    getNextCell: function() {
        var index = this.getColIndex();
        return this.row.cells[index + 1];
    }
};

;

Row = function() {
    this.cells = [];
};

Row.prototype = {
    addCell: function(cell) {
        cell.row = this;
        cell.grid = this.grid;
        this.cells.push(cell);
    },

    getIndex: function() {
        for (var i = 0; i < this.grid.rows.length; i++) {
            if (this.grid.rows[i] == this) {
                return i;
            }
        }
    },

    insertRowBeneath: function() {
        var row = new Row();
        row.grid = this.grid;

        for (var i = 0; i < this.grid.colCount; i++) {
            row.addCell(new Cell());
        }

        var rowIndex = this.getIndex();
        var rows = this.grid.rows;

        if (rowIndex == rows.length - 1) {
            rows.push(row);
        } else {
            rows.splice(rowIndex + 1, 0, row);
        }

        this.grid.rowCount = rows.length;
    },

    insertRowAbove: function() {
        var row = new Row();
        row.grid = this.grid;

        for (var i = 0; i < this.grid.colCount; i++) {
            row.addCell(new Cell());
        }

        var rowIndex = this.getIndex();
        var rows = this.grid.rows;

        if (rowIndex == 0) {
            rows.unshift(row);
        } else {
            rows.splice(rowIndex, 0, row);
        }

        this.grid.rowCount = rows.length;
    },

    getPrevRow: function() {
        var index = this.getIndex();
        if (index > 0) {
            return this.grid.rows[index - 1];
        } else {
            return null;
        }
    },

    getNextRow: function() {
        var index = this.getIndex();
        if (index < this.grid.rows.length) {
            return this.grid.rows[index + 1];
        } else {
            return null;
        }
    },

    tryInterleaveWith: function(other) {
        if (!this.isInterleaveWith(other)) {
            return false;
        }

        for (var i = 0; i < this.cells.length; i++) {
            var cell = this.cells[i];
            var otherCell = other.cells[i];

            if (cell.isFilled()) {
                other.cells[i] = cell;
            } else if (cell.isUnpackable()) {
                otherCell.setPackable(false);
            }
        }

        this._remove();

        return true;
    },

    isInterleaveWith: function(other) {
        if (other == null || other == this) {
            return false;
        } else if (other.getPrevRow() != this && other.getNextRow() != this) {
            return false;
        }
        for (var i = 0; i < this.cells.length; i++) {
            var cell = this.cells[i];
            var otherCell = other.cells[i];
            if (cell.isUnpackable() && otherCell.isUnpackable()) {
                return false;
            }
        }
        return true;
    },

    _remove: function() {
        var index = this.getIndex();
        this.grid.rows.splice(index, 1);
        this.grid.rowCount--;
    }
};

;

PriorityQueue = function(center) {
    this.ce = center;
    this.items = [];
};

PriorityQueue.prototype = {
    add: function(element) {
        this.items.push(element);

        var len = this.items.length;
        for (var i = 0; i < len; i++) {
            for (var j = i; j < len; j++) {
                var elem1 = this.items[i];
                var elem2 = this.items[j];
                if (this.compareTo(elem1, elem2) > 0) {
                    this.items[i] = elem2;
                    this.items[j] = elem1;
                }
            }
        }
    },

    compareTo: function(elem1, elem2) {
        return this.ce.backwardDistanceTo(elem1) - this.ce.backwardDistanceTo(elem2);
    },

    contains: function(element) {
        return this.items.indexOf(element) != -1;
    }
};

;

EdgeLayouter = function(grid, edge) {
    this.grid = grid;
    this.edge = edge;

    edge.innerPoints = [];

    this.calculateGlobals();
    this.pickLayoutForEdge();
};

EdgeLayouter.prototype = {
    calculateGlobals: function() {
        this.source = this.edge.source;
        this.target = this.edge.target;

        this.sourceRelativeCenterX = this.source.w / 2;
        this.sourceRelativeCenterY = this.source.h / 2;
        this.targetRelativeCenterX = this.target.w / 2;
        this.targetRelativeCenterY = this.target.h / 2;

        this.sourceAbsoluteCenterX = this.source.x + this.sourceRelativeCenterX;
        this.sourceAbsoluteCenterY = this.source.y + this.sourceRelativeCenterY;
        this.targetAbsoluteCenterX = this.target.x + this.targetRelativeCenterX;
        this.targetAbsoluteCenterY = this.target.y + this.targetRelativeCenterY;

        this.sourceAbsoluteX = this.source.x;
        this.sourceAbsoluteY = this.source.y;
        this.sourceAbsoluteX2 = this.source.x + this.source.w;
        this.sourceAbsoluteY2 = this.source.y + this.source.h;

        this.targetAbsoluteX = this.target.x;
        this.targetAbsoluteY = this.target.y;
        this.targetAbsoluteX2 = this.target.x + this.target.w;
        this.targetAbsoluteY2 = this.target.y + this.target.h;

        this.sourceJoin = this.source.isJoin();
        this.sourceSplit = this.source.isSplit();
        this.targetJoin = this.target.isJoin();
        this.targetSplit = this.target.isSplit();

        this.backwards = this.sourceAbsoluteCenterX > this.targetAbsoluteCenterX;
    },

    pickLayoutForEdge: function() {
        // sourceX == targetX, up and down
        if (this.sourceAbsoluteCenterX == this.targetAbsoluteCenterX) {
            this.setEdgeDirectCenter();
            return;
        } else if (this.sourceAbsoluteCenterY == this.targetAbsoluteCenterY) {
            if (this.areCellsHorizontalFree()) {
                this.setEdgeDirectCenter();
            } else {
                this.setEdgeAroundTheCorner(true);
            }
            return;
        }

        if (this.sourceAbsoluteCenterX <= this.targetAbsoluteCenterX
                && this.sourceAbsoluteCenterY <= this.targetAbsoluteCenterY) {
            // target is right under
            if (this.sourcejoin && this.sourceSplit) {
                this.setEdgeStepRight();
                return;
            } else if (this.sourceSplit) {
                this.setEdge90DegreeRightUnderAntiClockwise();
                return;
            } else if (this.targetJoin) {
                this.setEdge90DegreeRightUnderClockwise();
                return;
            }
        } else if (this.sourceAbsoluteCenterX <= this.targetAbsoluteCenterX
                && this.sourceAbsoluteCenterY > this.targetAbsoluteCenterY) {
            // target is right above
            if (this.sourcejoin && this.sourceSplit) {
                this.setEdgeStepRight();
                return;
            } else if (this.sourceSplit) {
                this.setEdge90DegreeRightAboveClockwise();
                return;
            } else if (this.targetJoin) {
                this.setEdge90DegreeRightAboveAntiClockwise();
                return;
            }
        }

        if (this.sourceJoin && sourceSplit && (!this.backwards)) {
            this.setEdgeStepRight();
            return;
        }

        if (this.sourceJoin && sourceSplit) {
            this.setEdgeAroundTheCorner(true);
            return;
        }

        this.setEdgeDirectCenter();
    },

    areCellsHorizontalFree: function() {
        var fromCell = null;
        var toCell = null;
        if (this.sourceAbsoluteCenterX < this.targetAbsoluteCenterX) {
            fromCell = this.grid.getCellOfItem(this.source);
            toCell = this.grid.getCellOfItem(this.target);
        } else {
            fromCell = this.grid.getCellOfItem(this.target);
            toCell = this.grid.getCellOfItem(this.source);
        }

        fromCell = fromCell.getNextCell();
        while (fromCell != toCell) {
            if (fromCell == null || fromCell.isFilled()) {
                return false;
            }
            fromCell = fromCell.getNextCell();
        }

        return true;
    },

    setEdgeDirectCenter: function() {
        var boundsMinX = Math.min(this.sourceAbsoluteCenterX,
                                  this.targetAbsoluteCenterX);
        var boundsMinY = Math.min(this.sourceAbsoluteCenterY,
                                  this.targetAbsoluteCenterY);
        var boundsMaxX = Math.max(this.sourceAbsoluteCenterX,
                                  this.targetAbsoluteCenterX);
        var boundsMaxY = Math.max(this.sourceAbsoluteCenterY,
                                  this.targetAbsoluteCenterY);
        // this.edge.innerPoints = [];
    },

    setEdge90DegreeRightAboveClockwise: function() {
        this.edge.innerPoints = [
            [this.sourceAbsoluteCenterX, this.targetAbsoluteCenterY]
        ];
    },

    setEdge90DegreeRightAboveAntiClockwise: function() {
        this.edge.innerPoints = [
            [this.targetAbsoluteCenterX, this.sourceAbsoluteCenterY]
        ];
    },

    setEdge90DegreeRightUnderClockwise: function() {
        this.edge.innerPoints = [
            [this.targetAbsoluteCenterX, this.sourceAbsoluteCenterY]
        ];
    },

    setEdge90DegreeRightUnderAntiClockwise: function() {
        this.edge.innerPoints = [
            [this.sourceAbsoluteCenterX, this.targetAbsoluteCenterY]
        ];
    },

    setEdgeAroundTheCorner: function(down) {

        var height = Math.max(this.source.h / 2, this.target.h / 2) + 20;

        if (down) {
            height *= -1;
        }

        this.edge.innerPoints = [
            [this.sourceAbsoluteCenterX, this.sourceAbsoluteCenterY + height],
            [this.targetAbsoluteCenterX, this.sourceAbsoluteCenterY + height]
        ];
    }
};

Ext.ux.OneCombo = Ext.extend(Ext.form.ComboBox, {
    initComponent: function() {
        this.readOnly = true;
        this.displayField = 'text';
        this.valueField = 'text';
        this.triggerAction = 'all';
        this.mode = 'local';
        this.emptyText = 'Please Select...';

        this.store = new Ext.data.SimpleStore({
            expandData: true,
            fields: ['text']
        });
        this.store.loadData(this.data);

        Ext.ux.OneCombo.superclass.initComponent.call(this);
    }
});

Ext.reg('onecombo', Ext.ux.OneCombo);

Gef.ns("Gef.planner");
Gef.ns("Gef.planner.model");
Gef.ns("Gef.planner.figure");
Gef.ns("Gef.planner.editpart");

function registerClass(type, name, url, w, h){
    function capitaliseFirstLetter(string)
	{
		return string.charAt(0).toUpperCase() + string.slice(1);
	}

	var preType = type;
	Gef.planner.model[preType+"Model"] = Gef.extend(Gef.jbs.model.GenericImageModel, {
		type: preType,
		url: url,
		constructor: function(conf) {
            Gef.planner.model[preType+"Model"].superclass.constructor.call(this, conf);
			this.w = w;
			this.h = h;
        }
	});
		
	Gef.planner.figure[preType+"Figure"] = Gef.extend(Gef.jbs.figure.GenericImageFigure, {
	});

	Gef.planner.editpart[preType+"EditPart"] = Gef.extend(Gef.jbs.editpart.GenericImageEditPart, {
		_figureClassName: "Gef.planner.figure." + preType + "Figure"
	});

	Gef.jbs.JBSModelFactory.registerModel(type, "Gef.planner.model." + preType + "Model");
	Gef.jbs.JBSEditPartFactory.registerEditPart(type, "Gef.planner.editpart." + preType + "EditPart");

}

var tpls = [
	["exchange", "exchange", "images/stenciles/huagong/huagong1/exchange.png", 37, 27],
	["fah", "fah", "images/stenciles/huagong/huagong1/fah.png",23,14],
	["fav", "fav", "images/stenciles/huagong/huagong1/fav.png",14,23],
	["guan", "guan", "images/stenciles/huagong/huagong1/guan.png",36,45],
	["lu", "lu", "images/stenciles/huagong/huagong1/lu.png",157,240],
	["meter", "meter", "images/stenciles/huagong/huagong1/meter.png",75,46],
	["motor", "motor", "images/stenciles/huagong/huagong1/motor.png",43,41],
	["motor2", "motor2", "images/stenciles/huagong/huagong1/motor2.png",43,41],
	["pressure", "pressure", "images/stenciles/huagong/huagong1/pressure.png",35,30],
	["wendu", "wendu", "images/stenciles/huagong/huagong1/wendu.png",35,30]
];

(function regClasses(arr){
    for(var i=0;i<tpls.length;i++){
	     registerClass.apply(this, tpls[i]);
    }
})(tpls);


function makePaletteArray(arr){
    var ret = [
	    {name: 'select',       image: 'scripts/gef/images/activities/select32.png',               title: '选择'},
        {name: 'transition',   image: 'scripts/gef/images/activities/32/flow_sequence.png',       title: '连线'},
		{name: 'rect',   image: 'scripts/gef/images/activities/draw_rectangle.png',       title: '矩形'},
		{name: 'ellipse',   image: 'scripts/gef/images/activities/draw_ellipse.png',       title: '椭圆'}
	
	];
	for(var i=0;i<tpls.length;i++){
	    ret.push({name: tpls[i][0],       image: tpls[i][2],               title: tpls[i][1]});
    }
	return ret;
}

function makeModelArray(arr){
    var ret = {
	    select: {
			text: 'select',
			creatable: false
		},
		transition: {
			text: 'transition',
			creatable: false,
			isConnection: true
		},rect: {
			text: 'rect',
			w: 80,
			h: 60
		},
		ellipse: {
			text: 'ellipse',
			w: 80,
			h: 60
		}
	};
	for(var i=0;i<tpls.length;i++){
	    ret[tpls[i][0]] = {
                text: tpls[i][0],
                w: tpls[i][3],
                h: tpls[i][4]
        };
    }
	return ret;
}

Gef.jbs.ExtPaletteHelper = Gef.extend(Gef.jbs.JBSPaletteHelper, {
    createSource: function() {
        return makeModelArray();
    },

    getSource: function() {
        if (!this.source) {
            this.source = this.createSource();
        }
        return this.source;
    },

    render: Gef.emptyFn,

    changeActivePalette: function(paletteConfig) {
        var el = null;
        if (this.getActivePalette()) {
            var oldActivePaletteId = this.getActivePalette().text;
            el = document.getElementById(oldActivePaletteId + '-img');
            el.style.border = '';
        }
        this.setActivePalette(paletteConfig);

        el = document.getElementById(paletteConfig.text + '-img');
        el.style.border = '1px dotted black';
    },

    resetActivePalette: function() {
        this.changeActivePalette({
            text: 'select'
        });
    },

    getPaletteConfig: function(p, t) {
        var id = t.parentNode.id;

        if (!id) {
            return null;
        }

        var source = this.getSource();
        var paletteConfig = this.getSource()[id];

        if (!paletteConfig) {
            return null;
        }

        this.changeActivePalette(paletteConfig);

        if (paletteConfig.creatable === false) {
            return null;
        }

        return paletteConfig;
    }
});


App.PalettePanel = Ext.extend(Ext.Panel, {
    initComponent: function() {
        this.region = 'west';
        this.title = '图元库';
        this.iconCls = 'tb-activity';
        this.width = 110;

        this.initPalette();

        App.PalettePanel.superclass.initComponent.call(this);
    },

    initPalette: function() {
        var paletteType = null;
        if (!Gef.PALETTE_TYPE) {
            paletteType = 'accordion';
        } else {
            paletteType = Gef.PALETTE_TYPE;
        }
        this.configLayout(paletteType);
        this.configItems(paletteType);
    },

    createHtml: function(array, divId) {
        if (divId) {
            var html = '<div id="' + divId + '" unselectable="on">';
        } else {
            var html = '<div unselectable="on">';
        }

        for (var i = 0; i < array.length; i++) {
            var item = array[i];
            html += '<div id="' + item.name + '" class="paletteItem-' + item.name
                + '" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on"><img width="32" height="32" id="'
                + item.name + '-img" class="paletteItem-' + item.name
                + '" src="' + item.image + '" unselectable="on"><br>'
                + item.title + '</div>';
        }
        html += '</div>';

        return html;
    },

    /**
     * this.layout = 'accordion';
     */
    configLayout: function(type) {
        if (!type || type == 'plain') {
            //
        } else if (type && type == 'accordion') {
            this.layout = 'accordion';
        }
    },

    configItems: function(type) {
        if (type && type == 'accordion') {
            this.createItemsForAccordion();
        } else if (!type || type == 'plain') {
            this.createItemsForHtml();
        }
    },

    createItemsForAccordion: function() {
        this.id = '__gef_jbs_palette__';

        this.items = [{
            title: '基础图元',
            iconCls: 'tb-activity',
            autoScroll: true,
            html: this.createHtml(makePaletteArray(tpls))
        }, {
            title: '高级图元',
            iconCls: 'tb-activity',
            autoScroll: true,
            html: ''
        }];
    },

    createItemsForHtml: function() {
        this.autoScroll = true;

        this.html = this.createHtml([
            {name: 'select',       image: 'select32',               title: '选择'},
            {name: 'transition',   image: '32/flow_sequence',       title: '连线'},
            {name: 'start',        image: '32/start_event_empty',   title: '开始'},
            {name: 'end',          image: '32/end_event_terminate', title: '结束'},
            {name: 'cancel',       image: '32/end_event_cancel',    title: '取消'},
            {name: 'error',        image: '32/end_event_error',     title: '错误'},
            {name: 'state',        image: '32/task_wait',           title: '等待'},
            {name: 'task',         image: '32/task_empty',          title: '任务'},
            {name: 'decision',     image: '32/gateway_exclusive',   title: '决策'},
            {name: 'fork',         image: '32/gateway_parallel',    title: '并行'},
            {name: 'join',         image: '32/gateway_parallel',    title: '汇聚'},
            {name: 'java',         image: '32/task_java',           title: 'JAVA'},
            {name: 'script',       image: '32/task_java',           title: '脚本'},
            {name: 'hql',          image: '32/task_hql',            title: 'HQL'},
            {name: 'sql',          image: '32/task_sql',            title: 'SQL'},
            {name: 'mail',         image: '32/task_empty',          title: '邮件'},
            {name: 'custom',       image: '32/task_empty',          title: '自定义'},
            {name: 'subProcess',   image: '32/task_empty',          title: '子流程'},
            {name: 'jms',          image: '32/task_empty',          title: 'JMS'},
            {name: 'ruleDecision', image: '32/gateway_exclusive',   title: '规则决策'},
            {name: 'rules',        image: '32/task_empty',          title: '规则'},
            {name: 'human',        image: '32/task_empty',          title: '人工节点'},
            {name: 'auto',         image: '32/task_empty',          title: '自动节点'},
            {name: 'counter-sign', image: '32/task_empty',          title: '会签节点'}
        ], '__gef_jbs_palette__');
    }
});

Ext.ux.TwoCombo = Ext.extend(Ext.form.ComboBox, {
    initComponent: function() {
        this.readOnly = true;
        this.displayField = 'text';
        this.valueField = 'value';
        this.triggerAction = 'all';
        this.mode = 'local';
        this.emptyText = 'Please Select...';

        this.store = new Ext.data.SimpleStore({
            fields: ['value', 'text']
        });
        this.store.loadData(this.data);

        Ext.ux.TwoCombo.superclass.initComponent.call(this);
    }
});

Ext.reg('twocombo', Ext.ux.TwoCombo);
