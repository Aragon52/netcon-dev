package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerNode
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CopperBevel
import com.example.ui.theme.CopperGlow
import com.example.ui.theme.CopperHighlight
import com.example.ui.theme.LedRed
import com.example.ui.theme.SteelBase
import com.example.ui.theme.SteelBorder
import com.example.ui.theme.SteelDeepInk
import com.example.ui.theme.SteelTraySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerManagerDrawer(
    open: Boolean,
    onDismiss: () -> Unit,
    nodes: List<ServerNode>,
    selectedId: String,
    onSelect: (ServerNode) -> Unit,
    onDelete: (String) -> Unit,
    onAddLink: (String) -> Unit,
    onRefreshPings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!open) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddDialog by remember { mutableStateOf(false) }
    var linkInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SteelBase,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SERVER DIRECTORY",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CopperHighlight
                )

                Row {
                    IconButton(onClick = onRefreshPings) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Test Latency",
                            tint = CopperGlow
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add server button
            Button(
                onClick = { showAddDialog = !showAddDialog },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CopperBevel,
                    contentColor = CopperHighlight
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("add_server_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "IMPORT / ADD CONFIG URI (VMESS / VLESS / TROJAN / SS)",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showAddDialog) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SteelDeepInk)
                        .padding(12.dp)
                ) {
                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = { linkInput = it },
                        placeholder = { Text("Paste vmess://, vless://, trojan://, or ss:// link...", fontSize = 11.sp, color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth().testTag("config_uri_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CopperAccent,
                            unfocusedBorderColor = SteelBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (linkInput.isNotEmpty()) {
                                onAddLink(linkInput)
                                linkInput = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CopperAccent,
                            contentColor = SteelDeepInk
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.align(Alignment.End).testTag("save_config_button")
                    ) {
                        Text("IMPORT CONFIG", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Server List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(nodes, key = { it.id }) { node ->
                    val isSelected = node.id == selectedId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SteelTraySurface else SteelDeepInk)
                            .border(1.dp, if (isSelected) CopperAccent else SteelBorder, RoundedCornerShape(10.dp))
                            .clickable { onSelect(node) }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(node.flagEmoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = node.name,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CopperHighlight else TextPrimary
                                    )
                                    Text(
                                        text = "${node.country} · ${node.protocol.uppercase()} · ${node.address}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (node.pingMs > 0) "${node.pingMs}ms" else "--",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CopperGlow
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = CopperAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDelete(node.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Node",
                                        tint = LedRed.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
