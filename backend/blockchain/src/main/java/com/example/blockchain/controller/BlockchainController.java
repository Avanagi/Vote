package com.example.blockchain.controller;

import com.example.blockchain.entity.TransactionEntity;
import com.example.blockchain.service.BlockchainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/blockchain")
@CrossOrigin(origins = "http://localhost:5173")
public class BlockchainController {

    private final BlockchainService blockchainService;

    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @PostMapping("/submitTransaction")
    public ResponseEntity<String> submitTransaction(@RequestBody TransactionEntity tx) {
        try {
            blockchainService.addTransaction(tx);
            return ResponseEntity.ok("Транзакция успешно добавлена.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при добавлении транзакции: " + e.getMessage());
        }
    }

    @PostMapping("/mine")
    public ResponseEntity<String> mineBlock() {
        try {
            blockchainService.mineBlock();
            return ResponseEntity.ok("Блок успешно замайнен.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при майнинге блока: " + e.getMessage());
        }
    }

    @GetMapping("/results")
    public Map<Long, Long> getResults(@RequestParam Long pollId) {
        return blockchainService.getResults(pollId);
    }

    @GetMapping("/validateBlockchain")
    public ResponseEntity<String> validateBlockchain() {
        try {
            boolean isValid = blockchainService.isBlockchainValid();
            if (isValid) {
                return ResponseEntity.ok("Цепочка блоков валидна.");
            } else {
                return ResponseEntity.status(400).body("Цепочка блоков невалидна.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при проверке цепочки блоков: " + e.getMessage());
        }
    }
}
