# Digital Autoimmune Monitor - Production Deployment Guide

**Application:** Digital Autoimmune Monitor
**Version:** 1.0.0 (Release)
**Runtime Requirement:** Java 17 or higher (Java 21/25 LTS recommended)

## Quick Start Launch Options

### Option A: Standard Direct Launch (Windows)
Double-click `DigitalAutoimmuneMonitor.bat`.

### Option B: Command Line Launch
```bash
java -jar DigitalAutoimmuneMonitor.jar
```

### Option C: Direct Dashboard Launch (Bypass Login)
```bash
java -jar DigitalAutoimmuneMonitor.jar --dashboard
```

### Option D: Run Heuristic Audit Suite
```bash
java -jar DigitalAutoimmuneMonitor.jar --audit
```

## System Architecture
- Pure Java Desktop Swing application.
- Zero external libraries or runtime dependencies.
- Embedded heuristic simulation and antigen stimulation engines.
